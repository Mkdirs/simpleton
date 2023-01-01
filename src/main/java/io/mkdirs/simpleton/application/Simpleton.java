package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.*;
import io.mkdirs.simpleton.forest_builder.structure.IfStructure;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.ScopeContext;
import io.mkdirs.simpleton.scope.VariableHolder;

import java.util.*;

public class Simpleton {

    private final ScopeContext SCRIPT_CTX = new ScopeContext();
    private ScopeContext currentScope = SCRIPT_CTX;
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator(currentScope);


    public void execute(List<ASTNode> nodes){
        for(ASTNode node : nodes){
            execute(node);
        }
    }

    private void execute(ASTNode node){
        if(Token.LET_KW.equals(node.getToken())){
            String varName = node.left().getToken().getLiteral();
            Token type = node.right().getToken();
            if(currentScope.getVariable(varName).isPresent()){
                System.err.println("Variable "+varName+" is already declared !");
                return;
            }

            currentScope.pushVariable(varName, type);
        }else if(Token.EQUALS.equals(node.getToken())) {
            ASTNode left = node.left();
            Result<Token> exprRes = evaluator.evaluate(node.right(), true);

            if (Token.VARIABLE_NAME.equals(left.getToken())) {
                String varName = left.getToken().getLiteral();
                if (!currentScope.getVariable(varName).isPresent()) {
                    System.err.println("Undeclared variable " + varName + " !");
                    return;
                }

                if (exprRes.isFailure()) {
                    System.err.println(exprRes.getMessage());
                    return;
                }

                VariableHolder varHolder = currentScope.getVariable(varName).get();
                if (!varHolder.getType().equals(currentScope.typeOf(exprRes.get())) && !Token.NULL_KW.equals(currentScope.typeOf(exprRes.get()))) {
                    System.err.println("Expected type " + varHolder.getType() + " but instead got " + currentScope.typeOf(exprRes.get()));
                    return;
                }
                currentScope.getVariable(varName).get().setValue(exprRes.get());

            } else if (Token.LET_KW.equals(left.getToken())) {
                String varName = left.left().getToken().getLiteral();
                Token type = left.right() != null ? left.right().getToken() : null;

                if (currentScope.getVariable(varName).isPresent()) {
                    System.err.println("Variable " + varName + " is already declared !");
                    return;
                }

                if (exprRes.isFailure()) {
                    System.err.println(exprRes.getMessage());
                    return;
                }

                if (type == null) {
                    type = currentScope.typeOf(exprRes.get());
                    if (Token.NULL_KW.equals(type)) {
                        System.err.println("Cannot infer type of variable " + varName);
                        return;
                    }
                }


                if (!type.equals(currentScope.typeOf(exprRes.get())) && !Token.NULL_KW.equals(currentScope.typeOf(exprRes.get()))) {
                    System.err.println("Expected type " + type + " but instead got " + currentScope.typeOf(exprRes.get()));
                    return;
                }

                currentScope.pushVariable(varName, type, exprRes.get());

            }

        }else if(Token.IF_KW.equals(node.getToken())){
            Result<Token> exprRes = evaluator.evaluate(node.get(0));
            if(exprRes.isFailure()) {
                System.err.println(exprRes.getMessage());
                return;
            }

            if(!Token.BOOL_KW.equals(currentScope.typeOf(exprRes.get()))){
                System.err.println("Expected type "+ Token.BOOL_KW+" but instead got "+currentScope.typeOf(exprRes.get()));
                return;
            }

            if("true".equals(exprRes.get().getLiteral())){
                ASTNode body = node.get(1);
                currentScope = currentScope.child();
                evaluator.setScopeContext(currentScope);

                execute(body.getChildren());

                currentScope = currentScope.getParent();
                evaluator.setScopeContext(currentScope);
            }else if(node.get(2) != null){
                ASTNode elseBody = node.get(2).get(0);
                currentScope = currentScope.child();
                evaluator.setScopeContext(currentScope);

                execute(elseBody.getChildren());

                currentScope = currentScope.getParent();
                evaluator.setScopeContext(currentScope);
            }
        }else if(Token.FUNC.equals(node.getToken())){
            Result<Token> r = evaluator.evaluate(node);
            if(r.isFailure())
                System.err.println(r.getMessage());
        }
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
            //String next = i+1 < candidates.length ? candidates[i+1] : "";

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

        //return tokens.subList(0, candidates.length).stream().map(Token::getName).toList().containsAll(Arrays.asList(candidates));
        //return Arrays.toString(tokens.stream().map(Token::getName).toArray()).equals(statement.replace(" ", ", "));
    }

}
