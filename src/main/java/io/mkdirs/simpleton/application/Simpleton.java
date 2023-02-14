package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.*;
import io.mkdirs.simpleton.forest_builder.structure.FunctionStructure;
import io.mkdirs.simpleton.forest_builder.structure.IfStructure;
import io.mkdirs.simpleton.forest_builder.structure.WhileStructure;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.Token;
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


    public Result execute(List<ASTNode> nodes){
        Result lastResult = null;

        for(ASTNode node : nodes){
            lastResult = execute(node);

            if(lastResult.isFailure() || lastResult.isTerminative())
                return lastResult;
        }

        if(lastResult == null)
            return Result.success(Token.VOID_KW);

        return lastResult;
    }

    private Result execute(ASTNode node){
        if(Token.LET_KW.equals(node.getToken())){
            String varName = node.left().getToken().getLiteral();
            Type type = Type.typeOf(node.right().getToken());
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
                if (!varHolder.getType().equals(Type.typeOf(exprRes.get())) && !Token.NULL_KW.equals(Type.typeOf(exprRes.get()))) {
                    return Result.failure("Expected type " + varHolder.getType() + " but instead got " + Type.typeOf(exprRes.get()));
                }
                currentScope.getVariable(varName).get().setValue(exprRes.get());

            } else if (Token.LET_KW.equals(left.getToken())) {
                String varName = left.left().getToken().getLiteral();
                Type type = left.right() != null ? Type.typeOf(left.right().getToken()) : null;

                if (currentScope.getVariable(varName).isPresent()) {
                    return Result.failure("Variable " + varName + " is already declared !");
                }

                if (exprRes.isFailure()) {
                    return Result.failure(exprRes.getMessage());
                }

                if (type == null) {
                    type = Type.typeOf(exprRes.get());
                    if (Type.NULL.equals(type)) {
                        return Result.failure("Cannot infer type of variable " + varName);
                    }
                }


                if (!type.equals(Type.typeOf(exprRes.get())) && !Type.NULL.equals(Type.typeOf(exprRes.get()))) {
                    return Result.failure("Expected type " + type + " but instead got " + Type.typeOf(exprRes.get()));
                }

                currentScope.pushVariable(varName, type, exprRes.get());

            }

        }else if(Token.IF_KW.equals(node.getToken())) {
            Result<Token> exprRes = evaluator.evaluate(node.get(0));
            if (exprRes.isFailure()) {
                return Result.failure(exprRes.getMessage());
            }

            if (!Type.BOOLEAN.equals(Type.typeOf(exprRes.get()))) {
                return Result.failure("Expected type " + Type.BOOLEAN + " but instead got " + Type.typeOf(exprRes.get()));
            }

            if ("true".equals(exprRes.get().getLiteral())) {
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

        }else if(Token.WHILE_KW.equals(node.getToken())) {

            Result<Token> exprRes = evaluator.evaluate(node.get(0));
            if (exprRes.isFailure()) {
                return Result.failure(exprRes.getMessage());
            }

            if (!Type.BOOLEAN.equals(Type.typeOf(exprRes.get()))) {
                return Result.failure("Expected type " + Type.BOOLEAN + " but instead got " + Type.typeOf(exprRes.get()));
            }

            currentScope = currentScope.child();
            evaluator.setScopeContext(currentScope);
            ASTNode body = node.get(1);

            Result result = null;

            while ("true".equals(exprRes.get().getLiteral())) {

                result = execute(body.getChildren());

                if (result.isFailure() || result.isTerminative())
                    return result;


                exprRes = evaluator.evaluate(node.get(0));
                if (exprRes.isFailure()) {
                    return Result.failure(exprRes.getMessage());
                }

                if (!Type.BOOLEAN.equals(Type.typeOf(exprRes.get()))) {
                    return Result.failure("Expected type " + Type.BOOLEAN + " but instead got " + Type.typeOf(exprRes.get()));
                }

            }


            currentScope = currentScope.getParent();
            evaluator.setScopeContext(currentScope);

            if(result != null)
                return result;


        }else if(Token.DEF_KW.equals(node.getToken())) {

            if (Token.FUNCTION_KW.equals(node.get(0).getToken())) {
                ASTNode function = node.get(0);
                String name = function.get(0).getToken().getLiteral();
                List<Map.Entry<String, Type>> entries = function.getChildren().subList(1, function.getChildren().size() - 2).stream()
                        .map(n -> Map.entry(n.getToken().getLiteral(), Type.typeOf(n.get(0).getToken())))
                        .collect(Collectors.toList());

                HashMap<String, Type> params = new HashMap<>();
                for (Map.Entry<String, Type> entry : entries) {
                    params.put(entry.getKey(), entry.getValue());
                }

                Type returnType = Type.typeOf(function.get(function.getChildren().size() - 2).getToken());

                Location location = new Location(function.get(function.getChildren().size()-1));

                FuncSignature funcSign = new FuncSignature(name, params, returnType, location);

                if(currentScope.hasFunctionSign(funcSign))
                    return Result.failure("Function "+funcSign+" is already declared !");

                currentScope.pushFunctionSign(funcSign);
            }

        }else if(Token.RETURN_KW.equals(node.getToken())){
            Result res;
            if(node.isLeaf()){
                res = Result.success(Token.VOID_KW);
            }else{
                res = evaluator.evaluate(node.get(0));
            }
            res.setTerminative();

            return res;

        }else if(Token.FUNC.equals(node.getToken())){
            return evaluator.evaluate(node);

        }

        return Result.success(Token.VOID_KW);
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
                .next(new FunctionStructure(chain))
                .next(new ReturnInstruction(evaluator, chain))
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
