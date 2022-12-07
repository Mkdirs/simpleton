package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.VariableName;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.ScopeContext;
import io.mkdirs.simpleton.scope.VariableHolder;

import java.util.*;

public class Simpleton {

    private final ScopeContext SCRIPT_CTX = new ScopeContext();
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator(SCRIPT_CTX);

    public void execute(List<ASTNode> nodes){
        for(ASTNode node : nodes){
            execute(node);
        }
    }

    private void execute(ASTNode node){
        if(Token.LET_KW.equals(node.getToken())){
            String varName = node.left().getToken().getLiteral();
            Token type = node.right().getToken();
            if(SCRIPT_CTX.getVariable(varName).isPresent()){
                System.err.println("Variable "+varName+" is already declared !");
                return;
            }

            SCRIPT_CTX.pushVariable(varName, type);
        }else if(Token.EQUALS.equals(node.getToken())){
            ASTNode left = node.left();
            Result<Token> exprRes = evaluator.evaluate(node.right(), true);

            if(Token.VARIABLE_NAME.equals(left.getToken())){
                String varName = left.getToken().getLiteral();
                if(!SCRIPT_CTX.getVariable(varName).isPresent()){
                    System.err.println("Undeclared variable "+varName+" !");
                    return;
                }

                if(exprRes.isFailure()){
                    System.err.println(exprRes.getMessage());
                    return;
                }

                VariableHolder varHolder = SCRIPT_CTX.getVariable(varName).get();
                if(!varHolder.getType().equals(SCRIPT_CTX.typeOf(exprRes.get())) && !Token.NULL_KW.equals(SCRIPT_CTX.typeOf(exprRes.get()))){
                    System.err.println("Expected type "+varHolder.getType()+" but instead got "+SCRIPT_CTX.typeOf(exprRes.get()));
                    return;
                }
                SCRIPT_CTX.getVariable(varName).get().setValue(exprRes.get());

            }else if(Token.LET_KW.equals(left.getToken())){
                String varName = left.left().getToken().getLiteral();
                Token type = left.right() != null ? left.right().getToken() : null;

                if(SCRIPT_CTX.getVariable(varName).isPresent()){
                    System.err.println("Variable "+varName+" is already declared !");
                    return;
                }

                if(exprRes.isFailure()){
                    System.err.println(exprRes.getMessage());
                    return;
                }

                if(type == null) {
                    type = SCRIPT_CTX.typeOf(exprRes.get());
                    if(Token.NULL_KW.equals(type)){
                        System.err.println("Cannot infer type of variable "+varName);
                        return;
                    }
                }



                if(!type.equals(SCRIPT_CTX.typeOf(exprRes.get())) && !Token.NULL_KW.equals(SCRIPT_CTX.typeOf(exprRes.get()))  ){
                    System.err.println("Expected type "+type+" but instead got "+SCRIPT_CTX.typeOf(exprRes.get()));
                    return;
                }

                SCRIPT_CTX.pushVariable(varName, type, exprRes.get());

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

        while(!tokens.isEmpty()){

            int indexOfFirstEOL = tokens.indexOf(Token.EOL);
            Result<ASTNode> declarationTree = buildVarDeclarationTree(tokens, false);
            Result<ASTNode> assignmentTree = buildVarAssignmentTree(tokens);


            if(declarationTree.isSuccess()) {
                res.add(declarationTree.get());
                tokens = tokens.subList(indexOfFirstEOL+1, tokens.size());
            }else if(assignmentTree.isSuccess()) {
                res.add(assignmentTree.get());
                tokens = tokens.subList(indexOfFirstEOL + 1, tokens.size());
            }else {
                Result<ASTNode> exprRes =  evaluator.buildTree(tokens.subList(0, indexOfFirstEOL));
                if(exprRes.isFailure())
                    return Result.failure(exprRes.getMessage());


                res.add(exprRes.get());
                tokens = tokens.subList(indexOfFirstEOL+1, tokens.size());
            }


        }


        return Result.success(res);
    }

    private Result<ASTNode> buildVarAssignmentTree(List<Token> tokens){
        //Possible outcomes:
        //a = expr
        //let a = expr
        //let a : type = expr
        int indexOfFirstEOL = tokens.indexOf(Token.EOL);

        if(match(tokens, "variable_name equals * eol")){
            ASTNode root = new ASTNode(tokens.get(1));

            //add left variable
            root.addChild(new ASTNode(tokens.get(0)));

            //add right expression
            Result<ASTNode> res =  evaluator.buildTree(tokens.subList(2, indexOfFirstEOL));

            if(res.isFailure())
                return res;

            root.addChild(res.get());

            return Result.success(root);
        }

        if(match(tokens, "let_kw variable_name equals * eol")){
            ASTNode root = new ASTNode(tokens.get(2));

            Result<ASTNode> letTree = buildVarDeclarationTree(tokens.subList(0, 2), true);
            Result<ASTNode> exprRes = evaluator.buildTree(tokens.subList(3, indexOfFirstEOL));

            if(letTree.isFailure())
                return letTree;
            if(exprRes.isFailure())
                return exprRes;

            root.addChildren(letTree.get(), exprRes.get());

            return Result.success(root);
        }

        if(match(tokens, "let_kw variable_name colon type equals * eol")){
            ASTNode root = new ASTNode(tokens.get(4));

            Result<ASTNode> letTree = buildVarDeclarationTree(tokens.subList(0, 4), true);
            Result<ASTNode> exprRes = evaluator.buildTree(tokens.subList(5, indexOfFirstEOL));

            if(letTree.isFailure())
                return letTree;
            if(exprRes.isFailure())
                return exprRes;

            root.addChildren(letTree.get(), exprRes.get());

            return Result.success(root);
        }

        return Result.failure("Unable to process assigment");
    }

    private Result<ASTNode> buildVarDeclarationTree(List<Token> tokens, boolean fromAssignmentTreeBuilder){
        //Possible outcomes:
        //let a : type = expr   special case handled by varAssignmentTree tree builder
        //let a = expr          special case handled by ----------------- tree builder
        //let a : type
        //let a                 special case, semantically wrong but handled here anyway to make it easier for the assignment tree builder

        if(match(tokens, "let_kw variable_name colon type"+(fromAssignmentTreeBuilder ? "" : " eol") )) {
            //let_kw { variable_name, type}
            ASTNode root = new ASTNode(tokens.get(0));

            var varName = new ASTNode(tokens.get(1));
            var varType = new ASTNode(tokens.get(3));

            root.addChildren(varName, varType);

            return Result.success(root);
        }

        if(match(tokens, "let_kw variable_name") && fromAssignmentTreeBuilder){
            ASTNode root = new ASTNode(tokens.get(0));
            var varName = new ASTNode(tokens.get(1));

            root.addChild(varName);

            return Result.success(root);
        }

        return Result.failure("Unable to process declaration");
    }

    private static boolean match(List<Token> tokens, String statement){
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
