package io.mkdirs.simpleton.evaluator;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.operator.Operator;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.Value;
import io.mkdirs.simpleton.model.error.StackableError;
import io.mkdirs.simpleton.model.error.StackableErrorBuilder;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.model.token.composite.VariableName;
import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.FuncSignature;
import io.mkdirs.simpleton.scope.Location;
import io.mkdirs.simpleton.scope.ScopeContext;
import io.mkdirs.simpleton.scope.VariableHolder;

import java.util.*;

public class ExpressionEvaluator{

    ScopeContext scopeContext;

    public ExpressionEvaluator(ScopeContext scopeContext){
        this.scopeContext = scopeContext;
    }

    public void setScopeContext(ScopeContext scopeContext){this.scopeContext = scopeContext;}


    public Result<Value, StackableError> evaluate(ASTNode tree, Type expectedReturn){
        if(tree == null)
            return Result.success(null);

        if(tree.isLeaf()) {
            Token token = tree.getToken();
            if(TokenKind.VAR_NAME.equals(token.kind)){
                var varName = (VariableName) token;
                VariableHolder var = this.scopeContext.getVariable(varName.name).orElse(null);

                if(var == null)
                    return Result.failure(new StackableErrorBuilder("Variable '"+varName.name+"' does not exist")
                            .withStatement("")
                            .build()
                    );

                if(expectedReturn != null && !Type.ANY.equals(expectedReturn) && !expectedReturn.equals(var.getType()))
                    return Result.failure(new StackableErrorBuilder("Expected type "+expectedReturn+" but instead got "+var.getType())
                            .withStatement("")
                            .build()
                    );

                return Result.success(var.getValue());

            }else if(TokenKind.FUNC.equals(token.kind)){
                Func func = (Func) token;
                Result funcRes = func.computeArgs(this);

                if(funcRes.isFailure())
                    return funcRes;


                Result<FuncSignature, StackableError> signatureResult = this.scopeContext.getFunctionSign(func);

                if(signatureResult.isFailure())
                    return Result.failure(new StackableErrorBuilder(signatureResult.err().highlightError()).build());

                FuncSignature signature = signatureResult.get();

                if(expectedReturn != null && !Type.VOID.equals(expectedReturn) && Type.VOID.equals(signature.getReturnType()))
                    return Result.failure(new StackableErrorBuilder("Expected a return value but "+signature+" has return type "+Type.VOID)
                            .withStatement("")
                            .build()
                    );

                Result<Value, StackableError> res;

                if(Location.BUILTINS == signature.getLocation()){
                    res = this.scopeContext.getNativeFuncExecutor().execute(func);

                }else{
                    ScopeContext other = this.scopeContext.child();
                    int i = 0;
                    for(Map.Entry<String, Type> entry : signature.getArgs().entrySet()){
                        other.pushVariable(entry.getKey(), entry.getValue(), func.getArgs().get(i));
                        i++;
                    }

                    Simpleton simpleton = new Simpleton(other);
                    ASTNode body = signature.getLocation().getBody();
                    res = simpleton.execute(body.getChildren());
                }

                if(res.isFailure())
                    return res;


                Value val = res.get();
                if(Type.VOID.equals(signature.getReturnType()) && !Type.VOID.equals(val.type()))
                    return Result.failure(new StackableErrorBuilder(token.toText()+" has a return type of "+Type.VOID+". You cannot return a value here")
                            .withStatement("")
                            .build()
                    );

                if(!signature.getReturnType().equals(val.type()) && !Type.NULL.equals(val.type()) && !Type.ANY.equals(signature.getReturnType()))
                    return Result.failure(new StackableErrorBuilder("Unexpected error: "+token.toText()+" should return '"+signature.getReturnType().name()+"' but instead returned '"+val.type()+"'")
                            .withStatement("")
                            .build()
                    );

                if(expectedReturn != null && !Type.ANY.equals(expectedReturn) && !expectedReturn.equals(val.type()))
                    return Result.failure(new StackableErrorBuilder("Expected type "+expectedReturn+" but instead got "+val.type())
                            .withStatement("")
                            .build()
                    );


                return Result.success(res.get());


            }
            if(TokenKind.NULL_KW.equals(tree.getToken().kind))
                return Result.success(Value.NULL);

            if(expectedReturn != null && !Type.ANY.equals(expectedReturn) && !expectedReturn.equals(Type.typeOf(tree.getToken().kind)))
                return Result.failure(new StackableErrorBuilder("Expected type "+expectedReturn+" but instead got "+Type.typeOf(tree.getToken().kind))
                        .withStatement("")
                        .build()
                );


            var type = Type.typeOf(tree.getToken().kind);
            return Result.success(new Value(type, ((LiteralValueToken)tree.getToken()).value));
        }



        var left = evaluate(tree.left());

        if(left.isFailure())
            return left;


        var right = evaluate(tree.right());

        if(right.isFailure())
            return right;



        Optional<Operator> operator = getOperator(tree.getToken().kind);

        if(operator.isPresent()){
            Optional<Value> r = operator.get().evaluate(left.get(), right.get());
            if(r.isEmpty())
                return Result.failure(new StackableErrorBuilder("Unable to apply '"+operator.get().getTokenKind().literal+"' on '"+left.get().type()+"' and '"+right.get().type()+"'")
                        .withStatement("")
                        .build()
                );

            if(expectedReturn != null && !Type.ANY.equals(expectedReturn) && !expectedReturn.equals(r.get().type()))
                return Result.failure(new StackableErrorBuilder("Expected type "+expectedReturn+" but instead got "+r.get().type())
                        .withStatement("")
                        .build()
                );

            return Result.success(r.get());
        }

        return Result.failure(new StackableErrorBuilder("Unknown operator: \""+tree.getToken().kind.literal+"\"")
                .withStatement("")
                .build()
        );
    }

    public Result<Value, StackableError> evaluate(ASTNode tree){return evaluate(tree, null);}

    private Optional<Operator> getOperator(TokenKind tokenKind){
        return Arrays.stream(Operator.OPERATORS).filter(e  -> e.getTokenKind().equals(tokenKind)).findFirst();
    }

    public Result<ASTNode, StackableError> buildTree(List<Token> tokens){

        if(tokens.isEmpty())
            return Result.success(null);
        else if(tokens.size() == 1)
            return Result.success(new ASTNode(tokens.get(0)));

        int closingParenthesis = getClosingParenthesis(0, tokens);

        if(closingParenthesis != -1 && closingParenthesis == tokens.size()-1)
            return buildTree(tokens.subList(1, closingParenthesis));

        int mainOperatorIndex = findMainOperatorIndex(tokens);

        if(mainOperatorIndex == -1)
            return Result.failure(new StackableErrorBuilder("Unexpected error")
                    .withStatement("")
                    .build()
            );


        Token operator = tokens.get(mainOperatorIndex);
        var left = buildTree(tokens.subList(0, mainOperatorIndex));
        if(left.isFailure())
            return left;

        var right = buildTree(tokens.subList(mainOperatorIndex+1, tokens.size()));
        if(right.isFailure())
            return right;

        ASTNode tree = new ASTNode(operator);
        tree.addChild(left.get());
        tree.addChild(right.get());

        return Result.success(tree);
    }

    private int getClosingParenthesis(int start, List<Token> tokens){
        if(!TokenKind.L_PAREN.equals(tokens.get(start).kind))
            return -1;

        int openParenthesis = 1;
        int i = start+1;
        while(i < tokens.size() && openParenthesis > 0){
            Token t = tokens.get(i);

            if(TokenKind.L_PAREN.equals(t.kind))
                openParenthesis++;
            else if(TokenKind.R_PAREN.equals(t.kind))
                openParenthesis--;

            i++;
        }

        if(openParenthesis > 0)
            return -1;

        return i-1;
    }

    private int findMainOperatorIndex(List<Token> tokens){
        int mainOperatorIndex = -1;
        int currentOperatorParenthesisScope = 1;
        int mainOperatorParenthesisScope = 1;
        final int parenthesisFactor = 5;

        for(int i = 0; i < tokens.size(); i++){
            Token token = tokens.get(i);

            if(token.kind.equals(TokenKind.L_PAREN)) {
                currentOperatorParenthesisScope++;
            }else if(token.kind.equals(TokenKind.R_PAREN)){
                currentOperatorParenthesisScope--;

                if(currentOperatorParenthesisScope < 1)
                    currentOperatorParenthesisScope = 1;
            }

            Optional<Operator> operator = Arrays.stream(Operator.OPERATORS).filter(e -> e.getTokenKind().equals(token.kind)).findFirst();
            if(operator.isPresent()){
                if(mainOperatorIndex == -1) {
                    mainOperatorIndex = i;
                    mainOperatorParenthesisScope = currentOperatorParenthesisScope;
                }else{
                    final int main = mainOperatorIndex;
                    Optional<Operator> mainOperatorOpt = Arrays.stream(Operator.OPERATORS).filter(e -> e.getTokenKind().equals(tokens.get(main).kind)).findFirst();

                    if(operator.get().getPriority() + (currentOperatorParenthesisScope*parenthesisFactor) <=  mainOperatorOpt.get().getPriority() + (mainOperatorParenthesisScope*parenthesisFactor)){
                        mainOperatorIndex = i;
                        mainOperatorParenthesisScope = currentOperatorParenthesisScope;
                    }
                }
            }

        }

        return mainOperatorIndex;
    }


}
