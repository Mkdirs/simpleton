package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.*;
import io.mkdirs.simpleton.forest_builder.structure.FunctionStructure;
import io.mkdirs.simpleton.forest_builder.structure.IfStructure;
import io.mkdirs.simpleton.forest_builder.structure.WhileStructure;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.FuncSignature;
import io.mkdirs.simpleton.scope.ScopeContext;
import io.mkdirs.simpleton.scope.VariableHolder;

import java.util.*;
import java.util.stream.Collectors;

public class Simpleton {

    private final ScopeContext SCRIPT_CTX = new ScopeContext();
    private ScopeContext currentScope = SCRIPT_CTX;
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator(currentScope);


    public Result execute(List<ASTNode> nodes){
        for(ASTNode node : nodes){
            var result = execute(node);
            if(result.isFailure())
                return result;
        }

        return Result.success(null);
    }

    private Result execute(ASTNode node){
        if(Token.LET_KW.equals(node.getToken())){
            String varName = node.left().getToken().getLiteral();
            Token type = node.right().getToken();
            if(currentScope.getVariable(varName).isPresent()){
                return Result.failure("Variable "+varName+" is already declared !");
            }

            currentScope.pushVariable(varName, type);
        }else if(Token.EQUALS.equals(node.getToken())) {
            ASTNode left = node.left();
            Result<Token> exprRes = evaluator.evaluate(node.right(), true);

            if (Token.VARIABLE_NAME.equals(left.getToken())) {
                String varName = left.getToken().getLiteral();
                if (!currentScope.getVariable(varName).isPresent()) {
                    return Result.failure("Undeclared variable " + varName + " !");
                }

                if (exprRes.isFailure()) {
                    return Result.failure(exprRes.getMessage());
                }

                VariableHolder varHolder = currentScope.getVariable(varName).get();
                if (!varHolder.getType().equals(currentScope.typeOf(exprRes.get())) && !Token.NULL_KW.equals(currentScope.typeOf(exprRes.get()))) {
                    return Result.failure("Expected type " + varHolder.getType() + " but instead got " + currentScope.typeOf(exprRes.get()));
                }
                currentScope.getVariable(varName).get().setValue(exprRes.get());

            } else if (Token.LET_KW.equals(left.getToken())) {
                String varName = left.left().getToken().getLiteral();
                Token type = left.right() != null ? left.right().getToken() : null;

                if (currentScope.getVariable(varName).isPresent()) {
                    return Result.failure("Variable " + varName + " is already declared !");
                }

                if (exprRes.isFailure()) {
                    return Result.failure(exprRes.getMessage());
                }

                if (type == null) {
                    type = currentScope.typeOf(exprRes.get());
                    if (Token.NULL_KW.equals(type)) {
                        return Result.failure("Cannot infer type of variable " + varName);
                    }
                }


                if (!type.equals(currentScope.typeOf(exprRes.get())) && !Token.NULL_KW.equals(currentScope.typeOf(exprRes.get()))) {
                    return Result.failure("Expected type " + type + " but instead got " + currentScope.typeOf(exprRes.get()));
                }

                currentScope.pushVariable(varName, type, exprRes.get());

            }

        }else if(Token.IF_KW.equals(node.getToken())) {
            Result<Token> exprRes = evaluator.evaluate(node.get(0));
            if (exprRes.isFailure()) {
                return Result.failure(exprRes.getMessage());
            }

            if (!Token.BOOL_KW.equals(currentScope.typeOf(exprRes.get()))) {
                return Result.failure("Expected type " + Token.BOOL_KW + " but instead got " + currentScope.typeOf(exprRes.get()));
            }

            if ("true".equals(exprRes.get().getLiteral())) {
                ASTNode body = node.get(1);
                currentScope = currentScope.child();
                evaluator.setScopeContext(currentScope);

                var result = execute(body.getChildren());
                if(result.isFailure())
                    return result;

                currentScope = currentScope.getParent();
                evaluator.setScopeContext(currentScope);
            } else if (node.get(2) != null) {
                ASTNode elseBody = node.get(2).get(0);
                currentScope = currentScope.child();
                evaluator.setScopeContext(currentScope);

                var result = execute(elseBody.getChildren());

                if(result.isFailure())
                    return result;

                currentScope = currentScope.getParent();
                evaluator.setScopeContext(currentScope);
            }

        }else if(Token.WHILE_KW.equals(node.getToken())) {

            Result<Token> exprRes = evaluator.evaluate(node.get(0));
            if (exprRes.isFailure()) {
                return Result.failure(exprRes.getMessage());
            }

            if (!Token.BOOL_KW.equals(currentScope.typeOf(exprRes.get()))) {
                return Result.failure("Expected type " + Token.BOOL_KW + " but instead got " + currentScope.typeOf(exprRes.get()));
            }

            currentScope = currentScope.child();
            evaluator.setScopeContext(currentScope);
            ASTNode body = node.get(1);


            while ("true".equals(exprRes.get().getLiteral())) {

                var result = execute(body.getChildren());

                if (result.isFailure())
                    return result;


                exprRes = evaluator.evaluate(node.get(0));
                if (exprRes.isFailure()) {
                    return Result.failure(exprRes.getMessage());
                }

                if (!Token.BOOL_KW.equals(currentScope.typeOf(exprRes.get()))) {
                    return Result.failure("Expected type " + Token.BOOL_KW + " but instead got " + currentScope.typeOf(exprRes.get()));
                }

            }


            currentScope = currentScope.getParent();
            evaluator.setScopeContext(currentScope);


        }else if(Token.DEF_KW.equals(node.getToken())){

            if(Token.FUNCTION_KW.equals(node.get(0))){
                ASTNode function = node.get(0);
                String name = function.get(0).getToken().getLiteral();
                List<Map.Entry<String, Token>> entries = function.getChildren().subList(1, function.getChildren().size()-2).stream()
                        .map(n -> Map.entry(n.getToken().getLiteral(), n.get(0).getToken()))
                        .collect(Collectors.toList());

                HashMap<String, Token> params = new HashMap<>();
                for(Map.Entry<String, Token> entry : entries){
                    params.put(entry.getKey(), entry.getValue());
                }

                Token returnType = function.get(function.getChildren().size()-2).getToken();
                //Add location
                FuncSignature funcSign = new FuncSignature(name, params, returnType);

                currentScope.pushFunctionSign(funcSign);
            }

        }else if(Token.FUNC.equals(node.getToken())){
            Result<Token> r = evaluator.evaluate(node);
            if(r.isFailure())
                return r;
        }

        return Result.success(null);
    }


    public Result<List<Token>> buildTokens(String text){
        String[] lines = text.split("\n");
        int lineIndex = 0;

        Lexer lexer = new Lexer();
        List<Token> res = new ArrayList<>();

        while(lineIndex < lines.length){
            String line = lines[lineIndex];
            evaluator.setStatement(line);
            if(line.isBlank()){
                lineIndex++;
                continue;
            }

            Result<List<Token>> tokensResult = lexer.parse(line);
            if(tokensResult.isFailure()){
                return Result.failure(tokensResult.getMessage());
            }

            res.addAll(tokensResult.get());
            res.add(Token.EOL);


            lineIndex++;
        }


        return Result.success(res);
    }


    public Result<List<ASTNode>> buildTrees(List<Token> tokens){

        List<ASTNode> res = new ArrayList<>();
        final TreeBuilder chain = new FullVariableInitialization(evaluator);
        chain.next(new PartialVariableInitialization(evaluator, chain))
                .next(new VariableDeclaration(chain))
                .next(new VariableAssignment(evaluator, chain))
                .next(new IfStructure(evaluator, chain))
                .next(new WhileStructure(evaluator, chain))
                .next(new FunctionStructure())
                .next(new StandaloneExpression(evaluator, chain));

        while(!tokens.isEmpty()){

            TreeBuilderResult result = chain.build(tokens);

            if(result.tree().isSuccess()){
                res.add(result.tree().get());
                tokens = tokens.subList(result.jumpIndex(), tokens.size());
            }else{
                return Result.failure(result.tree().getMessage());
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

            if(!candidate.equalsIgnoreCase(token.getName()) && !candidate.equalsIgnoreCase(token.group())) {
                if(!acceptAny)
                    return false;
            }else
                acceptAny = false;



            i++;

        }while(i < candidates.length);


        return true;
    }

}
