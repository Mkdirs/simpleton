package io.mkdirs.simpleton.evaluator;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.operator.Operator;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.result.ResultProvider;
import io.mkdirs.simpleton.scope.FuncSignature;
import io.mkdirs.simpleton.scope.Location;
import io.mkdirs.simpleton.scope.ScopeContext;
import io.mkdirs.simpleton.scope.VariableHolder;

import java.util.*;

public class ExpressionEvaluator extends ResultProvider {

    ScopeContext scopeContext;

    public ExpressionEvaluator(ScopeContext scopeContext){
        this.scopeContext = scopeContext;
    }

    public void setScopeContext(ScopeContext scopeContext){this.scopeContext = scopeContext;}


    public Result<Token> evaluate(ASTNode tree, boolean assignVariable){
        if(tree == null)
            return Result.success(null);

        if(tree.isLeaf()) {
            Token token = tree.getToken();
            if(Token.VARIABLE_NAME.equals(token)){
                VariableHolder var = this.scopeContext.getVariable(token.getLiteral()).orElse(null);

                if(var == null)
                    return Result.failure("Variable '"+token.getLiteral()+"' does not exist");

                return Result.success(var.getValue());

            }else if(Token.FUNC.equals(token)){
                Func func = (Func) token;
                Result funcRes = func.computeArgs(this);

                if(funcRes.isFailure())
                    return funcRes;



                FuncSignature signature = this.scopeContext.getFunctionSign(func).orElse(null);

                if(signature == null)
                    return Result.failure("Function '"+(func.toText())+"' does not exist");

                if(assignVariable && Token.VOID_KW.equals(signature.getReturnType()))
                    return Result.failure("No value returned !");

                if(Location.BUILTINS == signature.getLocation()){
                    Result<Token> r = this.scopeContext.getNativeFuncExecutor().execute(func);
                    if(r.isFailure())
                        return r;


                    if(!signature.getReturnType().equals(Type.typeOf(r.get())))
                        return Result.failure("Unexpected error: "+token.toText()+" should return '"+signature.getReturnType().name()+"' but instead returned '"+Type.typeOf(r.get())+"'");

                    return Result.success(r.get());
                }else{
                    ScopeContext other = this.scopeContext.child();
                    int i = 0;
                    for(Map.Entry<String, Type> entry : signature.getArgs().entrySet()){
                        other.pushVariable(entry.getKey(), entry.getValue(), func.getArgs().get(i));
                        i++;
                    }

                    Simpleton simpleton = new Simpleton(other);
                    ASTNode body = signature.getLocation().getBody();
                    Result r = simpleton.execute(body.getChildren());

                    if(r.isFailure())
                        return r;

                    Token tok = (Token) r.get();
                    if(!signature.getReturnType().equals(Type.typeOf(tok)))
                        return Result.failure("Unexpected error: "+token.toText()+" should return '"+signature.getReturnType().name()+"' but instead returned '"+Type.typeOf(tok)+"'");


                    return Result.success(tok);
                }


            }
            return Result.success(tree.getToken());
        }



        Result<Token> left = evaluate(tree.left());

        if(left.isFailure())
            return pushError(left.getMessage());


        Result<Token> right = evaluate(tree.right());

        if(right.isFailure())
            return pushError(right.getMessage());



        Optional<Operator> operator = getOperator(tree.getToken());

        if(operator.isPresent()){
            Optional<Token> r = operator.get().evaluate(left.get(), right.get());
            if(r.isEmpty())
                return pushError("Unable to apply '"+operator.get().getToken().getLiteral()+"' on '"+left.get()+"' and '"+right.get()+"'");

            return Result.success(r.get());
        }

        return pushError("Unknown operator: \""+tree.getToken().getLiteral()+"\"");
    }

    public Result<Token> evaluate(ASTNode tree){return evaluate(tree, false);}

    private Optional<Operator> getOperator(Token token){
        return Arrays.stream(Operator.OPERATORS).filter(e  -> e.getToken().equals(token)).findFirst();
    }

    public Result<ASTNode> buildTree(List<Token> tokens){

        if(tokens.isEmpty())
            return Result.success(null);
        else if(tokens.size() == 1)
            return Result.success(new ASTNode(tokens.get(0)));

        int closingParenthesis = getClosingParenthesis(0, tokens);

        if(closingParenthesis != -1 && closingParenthesis == tokens.size()-1)
            return buildTree(tokens.subList(1, closingParenthesis));

        int mainOperatorIndex = findMainOperatorIndex(tokens);

        if(mainOperatorIndex == -1)
            return pushError("Unexpected error");


        Token operator = tokens.get(mainOperatorIndex);
        Result<ASTNode> left = buildTree(tokens.subList(0, mainOperatorIndex));
        Result<ASTNode> right = buildTree(tokens.subList(mainOperatorIndex+1, tokens.size()));

        ASTNode tree = new ASTNode(operator);
        tree.addChild(left.get());
        tree.addChild(right.get());

        return Result.success(tree);
    }

    private int getClosingParenthesis(int start, List<Token> tokens){
        if(!Token.L_PAREN.equals(tokens.get(start)))
            return -1;

        int openParenthesis = 1;
        int i = start+1;
        while(i < tokens.size() && openParenthesis > 0){
            Token t = tokens.get(i);

            if(Token.L_PAREN.equals(t))
                openParenthesis++;
            else if(Token.R_PAREN.equals(t))
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

            if(token.equals(Token.L_PAREN)) {
                currentOperatorParenthesisScope++;
            }else if(token.equals(Token.R_PAREN)){
                currentOperatorParenthesisScope--;

                if(currentOperatorParenthesisScope < 1)
                    currentOperatorParenthesisScope = 1;
            }

            Optional<Operator> operator = Arrays.stream(Operator.OPERATORS).filter(e -> e.getToken().equals(token)).findFirst();
            if(operator.isPresent()){
                if(mainOperatorIndex == -1) {
                    mainOperatorIndex = i;
                    mainOperatorParenthesisScope = currentOperatorParenthesisScope;
                }else{
                    final int main = mainOperatorIndex;
                    Optional<Operator> mainOperatorOpt = Arrays.stream(Operator.OPERATORS).filter(e -> e.getToken().equals(tokens.get(main))).findFirst();

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
