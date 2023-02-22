package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.*;
import io.mkdirs.simpleton.forest_builder.structure.ForStructure;
import io.mkdirs.simpleton.forest_builder.structure.FunctionStructure;
import io.mkdirs.simpleton.forest_builder.structure.IfStructure;
import io.mkdirs.simpleton.forest_builder.structure.WhileStructure;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.Value;
import io.mkdirs.simpleton.model.error.ErrorStack;
import io.mkdirs.simpleton.model.error.StackableError;
import io.mkdirs.simpleton.model.error.StackableErrorBuilder;
import io.mkdirs.simpleton.model.token.EOL;
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
import java.util.stream.Collectors;

public class Simpleton {

    private final ScopeContext SCRIPT_CTX;
    private ScopeContext currentScope;
    private final ExpressionEvaluator evaluator;

    public Simpleton(ScopeContext ctx){
        SCRIPT_CTX = ctx;
        currentScope = SCRIPT_CTX;
        evaluator = new ExpressionEvaluator(currentScope);
    }

    public Simpleton(){
        this(new ScopeContext());
    }


    public Result<Value, StackableError> execute(List<ASTNode> nodes){
        Result lastResult = null;

        for(ASTNode node : nodes){
            if(node == null)
                continue;

            lastResult = execute(node);

            if(lastResult.isFailure() || lastResult.isTerminative())
                return lastResult;
        }

        if(lastResult == null)
            return Result.success(Value.VOID);

        return lastResult;
    }

    private Result<Value, StackableError> execute(ASTNode node){
        if(TokenKind.LET_KW.equals(node.getToken().kind)){
            String varName = ((VariableName) node.left().getToken()).name;
            Type type = Type.typeOf(node.right().getToken().kind);
            if(currentScope.getVariable(varName).isPresent()){
                return Result.failure(new StackableErrorBuilder("Variable "+varName+" is already declared !")
                        .withStatement("")
                        .build()
                );
            }

            currentScope.pushVariable(varName, type);
        }else if(TokenKind.EQUALS.equals(node.getToken().kind)) {
            ASTNode left = node.left();
            //Result<LiteralValueToken> exprRes = evaluator.evaluate(node.right(), true);

            if (TokenKind.VAR_NAME.equals(left.getToken().kind)) {
                String varName = ((VariableName)left.getToken()).name;
                if (!currentScope.getVariable(varName).isPresent()) {
                    return Result.failure(new StackableErrorBuilder("Undeclared variable " + varName + " !")
                            .withStatement("")
                            .build()
                    );
                }



                VariableHolder varHolder = currentScope.getVariable(varName).get();
                var exprRes = evaluator.evaluate(node.right(), varHolder.getType());

                if (exprRes.isFailure()) {
                    return exprRes;
                }

                /*if (!varHolder.getType().equals(Type.typeOf(exprRes.get().kind)) && !Type.NULL.equals(Type.typeOf(exprRes.get().kind))) {
                    return Result.failure("Expected type " + varHolder.getType() + " but instead got " + Type.typeOf(exprRes.get().kind));
                }

                 */
                VariableHolder v = currentScope.getVariable(varName).get();
                if(Type.ANY.equals(v.getType())){
                    Type t = exprRes.get().type();
                    currentScope.pushVariable(varName, t, exprRes.get());
                }else
                    currentScope.getVariable(varName).get().setValue(exprRes.get());

            } else if (TokenKind.LET_KW.equals(left.getToken().kind)) {
                String varName = ((VariableName)left.left().getToken()).name;
                Type type = left.right() != null ? Type.typeOf(left.right().getToken().kind) : Type.ANY;

                if (currentScope.getVariable(varName).isPresent()) {
                    return Result.failure(new StackableErrorBuilder("Variable " + varName + " is already declared !")
                            .withStatement("")
                            .build()
                    );
                }

                var exprRes = evaluator.evaluate(node.right(), type);

                if (exprRes.isFailure()) {
                    return exprRes;
                }

                if (Type.ANY.equals(type)) {
                    type = exprRes.get().type();
                    if (Type.NULL.equals(type)) {
                        if(TokenKind.FUNC.equals(node.right().getToken().kind)){
                            var signature = this.currentScope.getFunctionSign((Func) node.right().getToken()).get();
                            if(Type.ANY.equals(signature.getReturnType()))
                                return Result.failure(new StackableErrorBuilder("Cannot infer type of variable " + varName)
                                        .withStatement("")
                                        .build()
                                );
                            type = signature.getReturnType();
                        }else
                            return Result.failure(new StackableErrorBuilder("Cannot infer type of variable " + varName)
                                    .withStatement("")
                                    .build()
                            );
                    }
                }


                /*if (!type.equals(Type.typeOf(exprRes.get().kind)) && !Type.NULL.equals(Type.typeOf(exprRes.get().kind))) {
                    return Result.failure("totot Expected type " + type + " but instead got " + Type.typeOf(exprRes.get().kind));
                }

                 */

                currentScope.pushVariable(varName, type, exprRes.get());

            }

        }else if(TokenKind.IF_KW.equals(node.getToken().kind)) {
            var exprRes = evaluator.evaluate(node.get(0), Type.BOOLEAN);
            if (exprRes.isFailure()) {
                return exprRes;
            }


            if ("true".equals(exprRes.get().value())) {
                ASTNode body = node.get(1);
                currentScope = currentScope.child();
                evaluator.setScopeContext(currentScope);

                var result = execute(body.getChildren());

                currentScope = currentScope.getParent();
                evaluator.setScopeContext(currentScope);

                return result;
            } else if (node.get(2) != null) {
                ASTNode elseBody = node.get(2).get(0);
                currentScope = currentScope.child();
                evaluator.setScopeContext(currentScope);

                var result = execute(elseBody.getChildren());

                currentScope = currentScope.getParent();
                evaluator.setScopeContext(currentScope);
                return result;
            }

        }else if(TokenKind.WHILE_KW.equals(node.getToken().kind)) {

            var exprRes = evaluator.evaluate(node.get(0), Type.BOOLEAN);
            if (exprRes.isFailure()) {
                return exprRes;
            }


            currentScope = currentScope.child();
            evaluator.setScopeContext(currentScope);
            ASTNode body = node.get(1);

            Result<Value, StackableError> result = null;

            while ("true".equals(exprRes.get().value())) {

                result = execute(body.getChildren());

                if (result.isFailure() || result.isTerminative())
                    break;


                exprRes = evaluator.evaluate(node.get(0), Type.BOOLEAN);
                if (exprRes.isFailure()) {
                    return exprRes;
                }

                currentScope.flushVariables();

            }


            currentScope = currentScope.getParent();
            evaluator.setScopeContext(currentScope);

            if (result != null)
                return result;

        }else if(TokenKind.FOR_KW.equals(node.getToken().kind)){
            currentScope = currentScope.child();
            evaluator.setScopeContext(currentScope);

            var initRes = execute(node.get(0));
            if(initRes.isFailure())
                return initRes;


            var condRes = evaluator.evaluate(node.get(1), Type.BOOLEAN);
            if(condRes.isFailure())
                return condRes;


            currentScope = currentScope.child();
            evaluator.setScopeContext(currentScope);
            Result<Value, StackableError> result = null;
            while("true".equals(condRes.get().value())){
                result = execute(node.get(2).getChildren());

                if(result.isFailure() || result.isTerminative())
                    break;

                condRes = evaluator.evaluate(node.get(1), Type.BOOLEAN);

                if(condRes.isFailure())
                    return condRes;

                currentScope.flushVariables();

            }

            currentScope = currentScope.getParent().getParent();
            evaluator.setScopeContext(currentScope);

            if(result != null)
                return result;


        }else if(TokenKind.DEF_KW.equals(node.getToken().kind)) {

            if (TokenKind.FUNCTION_KW.equals(node.get(0).getToken().kind)) {
                ASTNode function = node.get(0);
                Func func = (Func) function.get(0).getToken();
                String name = func.name;
                List<Map.Entry<String, Type>> entries = function.getChildren().subList(1, function.getChildren().size() - 2).stream()
                        .map(n -> Map.entry( ((VariableName)n.getToken()).name, Type.typeOf(n.get(0).getToken().kind)))
                        .collect(Collectors.toList());

                Map<String, Type> params = new LinkedHashMap<>();
                for (Map.Entry<String, Type> entry : entries) {
                    params.put(entry.getKey(), entry.getValue());
                }

                Type returnType = Type.typeOf(function.get(function.getChildren().size() - 2).getToken().kind);

                Location location = new Location(function.get(function.getChildren().size()-1));

                FuncSignature funcSign = new FuncSignature(name, params, returnType, location);

                if(currentScope.hasFunctionSign(funcSign))
                    return Result.failure(new StackableErrorBuilder("Function "+funcSign+" is already declared !")
                            .withStatement("")
                            .build()
                    );

                currentScope.pushFunctionSign(funcSign);
            }

        }else if(TokenKind.RETURN_KW.equals(node.getToken().kind)){
            Result<Value, StackableError> res;
            if(node.isLeaf()){
                res = Result.success(Value.VOID);
            }else{
                res = evaluator.evaluate(node.get(0));
            }
            res.setTerminative();

            return res;

        }else if(TokenKind.FUNC.equals(node.getToken().kind)){
            return evaluator.evaluate(node);

        }

        return Result.success(Value.VOID);
    }


    public Result<List<Token>, StackableError> buildTokens(String text){
        Lexer lexer = new Lexer(text);

        var tokensResult = lexer.parse();
        if(tokensResult.isFailure()){
            return tokensResult;
        }

        return Result.success(tokensResult.get());
    }


    public Result<List<ASTNode>, StackableError> buildTrees(List<Token> tokens){
        List<ASTNode> res = new ArrayList<>();
        final TreeBuilder chain = new FullVariableInitialization(evaluator);
        chain.next(new PartialVariableInitialization(evaluator, chain))
                .next(new VariableDeclaration(chain))
                .next(new VariableAssignment(evaluator, chain))
                .next(new IfStructure(evaluator, chain))
                .next(new WhileStructure(evaluator, chain))
                .next(new ForStructure(evaluator, chain))
                .next(new FunctionStructure(chain))
                .next(new ReturnInstruction(evaluator, chain))
                .next(new StandaloneExpression(evaluator, chain));

        while(!tokens.isEmpty()){

            TreeBuilderResult result = chain.build(tokens);

            if(result.tree().isSuccess()){
                res.add(result.tree().get());
                tokens = tokens.subList(result.jumpIndex(), tokens.size());
            }else{
                return Result.failure(new StackableErrorBuilder(result.tree().err().highlightError()).build());
            }


        }


        return Result.success(res);
    }


    public static boolean match(List<Token> tokens, String statement){
        String[] candidates = statement.split(" ");
        if(tokens.size() < candidates.length)
            return false;

        int i = 0;
        boolean acceptAny = false;
        do{
            String candidate = candidates[i];
            Token token = tokens.get(i);

            if("*".equals(candidate))
                acceptAny = true;

            if(!candidate.equalsIgnoreCase(token.kind.name()) && !token.kind.group.contains(candidate.toLowerCase())) {
                if(!acceptAny)
                    return false;
            }else
                acceptAny = false;



            i++;

        }while(i < candidates.length);


        return true;
    }

}
