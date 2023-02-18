package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.*;
import io.mkdirs.simpleton.forest_builder.structure.FunctionStructure;
import io.mkdirs.simpleton.forest_builder.structure.IfStructure;
import io.mkdirs.simpleton.forest_builder.structure.WhileStructure;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.EOL;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.model.token.composite.VariableName;
import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;
import io.mkdirs.simpleton.model.token.literal.VoidPlaceholder;
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


    public Result<LiteralValueToken> execute(List<ASTNode> nodes){
        Result lastResult = null;

        for(ASTNode node : nodes){
            lastResult = execute(node);

            if(lastResult.isFailure() || lastResult.isTerminative())
                return lastResult;
        }

        if(lastResult == null)
            return Result.success(VoidPlaceholder.VOID);

        return lastResult;
    }

    private Result<LiteralValueToken> execute(ASTNode node){
        if(TokenKind.LET_KW.equals(node.getToken().kind)){
            String varName = ((VariableName) node.left().getToken()).name;
            Type type = Type.typeOf(node.right().getToken().kind);
            if(currentScope.getVariable(varName).isPresent()){
                return Result.failure("Variable "+varName+" is already declared !");
            }

            currentScope.pushVariable(varName, type);
        }else if(TokenKind.EQUALS.equals(node.getToken().kind)) {
            ASTNode left = node.left();
            //Result<LiteralValueToken> exprRes = evaluator.evaluate(node.right(), true);

            if (TokenKind.VAR_NAME.equals(left.getToken().kind)) {
                String varName = ((VariableName)left.getToken()).name;
                if (!currentScope.getVariable(varName).isPresent()) {
                    return Result.failure("Undeclared variable " + varName + " !");
                }



                VariableHolder varHolder = currentScope.getVariable(varName).get();
                Result<LiteralValueToken> exprRes = evaluator.evaluate(node.right(), varHolder.getType());

                if (exprRes.isFailure()) {
                    return Result.failure(exprRes.getMessage());
                }

                /*if (!varHolder.getType().equals(Type.typeOf(exprRes.get().kind)) && !Type.NULL.equals(Type.typeOf(exprRes.get().kind))) {
                    return Result.failure("Expected type " + varHolder.getType() + " but instead got " + Type.typeOf(exprRes.get().kind));
                }

                 */
                currentScope.getVariable(varName).get().setValue(exprRes.get());

            } else if (TokenKind.LET_KW.equals(left.getToken().kind)) {
                String varName = ((VariableName)left.left().getToken()).name;
                Type type = left.right() != null ? Type.typeOf(left.right().getToken().kind) : Type.UNKNOWN;

                if (currentScope.getVariable(varName).isPresent()) {
                    return Result.failure("Variable " + varName + " is already declared !");
                }

                Result<LiteralValueToken> exprRes = evaluator.evaluate(node.right(), type);

                if (exprRes.isFailure()) {
                    return Result.failure(exprRes.getMessage());
                }

                if (Type.UNKNOWN.equals(type)) {
                    type = Type.typeOf(exprRes.get().kind);
                    if (Type.NULL.equals(type)) {
                        if(TokenKind.FUNC.equals(node.right().getToken().kind)){
                            var signature = this.currentScope.getFunctionSign((Func) node.right().getToken()).get();
                            type = signature.getReturnType();
                        }else
                            return Result.failure("Cannot infer type of variable " + varName);
                    }
                }


                /*if (!type.equals(Type.typeOf(exprRes.get().kind)) && !Type.NULL.equals(Type.typeOf(exprRes.get().kind))) {
                    return Result.failure("totot Expected type " + type + " but instead got " + Type.typeOf(exprRes.get().kind));
                }

                 */

                currentScope.pushVariable(varName, type, exprRes.get());

            }

        }else if(TokenKind.IF_KW.equals(node.getToken().kind)) {
            Result<LiteralValueToken> exprRes = evaluator.evaluate(node.get(0), Type.BOOLEAN);
            if (exprRes.isFailure()) {
                return Result.failure(exprRes.getMessage());
            }


            if ("true".equals(exprRes.get().value)) {
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

            Result<LiteralValueToken> exprRes = evaluator.evaluate(node.get(0), Type.BOOLEAN);
            if (exprRes.isFailure()) {
                return Result.failure(exprRes.getMessage());
            }


            currentScope = currentScope.child();
            evaluator.setScopeContext(currentScope);
            ASTNode body = node.get(1);

            Result<LiteralValueToken> result = null;

            while ("true".equals(exprRes.get().value)) {

                result = execute(body.getChildren());

                if (result.isFailure() || result.isTerminative())
                    return result;


                exprRes = evaluator.evaluate(node.get(0), Type.BOOLEAN);
                if (exprRes.isFailure()) {
                    return Result.failure(exprRes.getMessage());
                }

            }


            currentScope = currentScope.getParent();
            evaluator.setScopeContext(currentScope);

            if(result != null)
                return result;


        }else if(TokenKind.DEF_KW.equals(node.getToken().kind)) {

            if (TokenKind.FUNCTION_KW.equals(node.get(0).getToken().kind)) {
                ASTNode function = node.get(0);
                VariableName funcName = (VariableName) function.get(0).getToken();
                String name = funcName.name;
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
                    return Result.failure("Function "+funcSign+" is already declared !");

                currentScope.pushFunctionSign(funcSign);
            }

        }else if(TokenKind.RETURN_KW.equals(node.getToken().kind)){
            Result res;
            if(node.isLeaf()){
                res = Result.success(VoidPlaceholder.VOID);
            }else{
                res = evaluator.evaluate(node.get(0));
            }
            res.setTerminative();

            return res;

        }else if(TokenKind.FUNC.equals(node.getToken().kind)){
            return evaluator.evaluate(node);

        }

        return Result.success(VoidPlaceholder.VOID);
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
            res.add(new EOL());


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
