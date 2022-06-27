package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.ScopeContext;

import java.util.*;

public class Simpleton {

    private static ScopeContext scope = new ScopeContext();
    private static final ExpressionEvaluator evaluator = new ExpressionEvaluator(scope);

    public static void run(String text){
        String[] lines = text.split("\n");

        Lexer lexer = new Lexer(scope);

        for(String line : lines){
            if(line.isBlank())
                continue;

            scope.setLine(line);
            Result<List<Token>> lexerResult = lexer.parse();

            if(lexerResult.isFailure()){
                System.out.println(lexerResult.getMessage());
                break;
            }

            List<Token> tokens = lexerResult.get();
            System.out.println(String.join(",", lexerResult.get().stream().map(Objects::toString).toList()));

            checkForVariableDeclaration(tokens);
            checkForVariableAssignation(tokens);

            System.out.println();

        }
    }

    private static void assignVariable(String name, List<Token> tokens, Token expectedType){
        Result<ASTNode> evaluatorBuildResult = evaluator.buildTree(tokens);

        if(evaluatorBuildResult.isFailure()){
            System.out.println(evaluatorBuildResult.getMessage());
            return;
        }

        Result<Token> evaluatorResult = evaluator.evaluate(evaluatorBuildResult.get());

        if(evaluatorResult.isFailure()){
            System.out.println(evaluatorResult.getMessage());
            return;
        }

        Token result = evaluatorResult.get();
        if(expectedType == null){
            scope.pushVariable(name, result, result.getLiteral());
        }else if(!expectedType.equals(result) && !Token.NULL_KW.equals(result)) {
            System.out.println("Expected '"+expectedType+"' but instead got '"+result+"'");
        }else{
            scope.pushVariable(name, expectedType, result.getLiteral());
        }

    }

    private static void assignVariable(String name, List<Token> tokens){assignVariable(name, tokens, null);}

    private static void checkForVariableDeclaration(List<Token> tokens){

        Token first = tokens.get(0);

        if(!first.equals(Token.LET_KW))
            return;

        HashMap<Token, Token> types = new HashMap<>();
        types.putAll(Map.ofEntries(
                Map.entry(Token.INT_KW, Token.INTEGER_LITERAL),
                Map.entry(Token.FLOAT_KW, Token.FLOAT_LITERAL),
                Map.entry(Token.CHAR_KW, Token.CHARACTER_LITERAL),
                Map.entry(Token.STRING_KW, Token.STRING_LITERAL),
                Map.entry(Token.BOOL_KW, Token.BOOLEAN_LITERAL)

        ));

        boolean assign = false;
        boolean inferType = false;
        Token type = null;
        if(tokens.size() >= 4 && tokens.subList(0, 3).equals(Arrays.asList(Token.LET_KW, Token.VARIABLE_NAME, Token.COLON))){
            if(!types.containsKey(tokens.get(3))){
                System.out.println("Unknown type '"+tokens.get(3)+"'");
                return;
            }

            type = types.get(tokens.get(3));

            if(tokens.size() > 5 && Token.EQUALS.equals(tokens.get(4))){
                assign = true;
            }else if(tokens.size() == 5 && Token.EQUALS.equals(tokens.get(4))){
                System.out.println("Expected an expression");
                return;
            }else if(tokens.size() >= 5 && !Token.EQUALS.equals(tokens.get(4))){
                System.out.println("Expected '"+Token.EQUALS+"' instead got '"+tokens.get(4)+"'");
                return;
            }


        }else if(tokens.size() >= 4 && tokens.subList(0, 3).equals(Arrays.asList(Token.LET_KW, Token.VARIABLE_NAME, Token.EQUALS))){
            assign = true;
            inferType = true;
        }else{
            System.out.println("Unexpected error !");
            return;
        }

        Token name = tokens.get(1);

        if(scope.getVariable(name.getLiteral()).isPresent()){
            System.out.println("Variable '"+name.getLiteral()+"' already exists");
            return;
        }

        if(!assign){
            scope.pushVariable(name.getLiteral(), type);
        }else if(!inferType){
            assignVariable(name.getLiteral(), tokens.subList(5,tokens.size()), type);
        }else{
            assignVariable(name.getLiteral(), tokens.subList(3, tokens.size()));
        }




        System.out.println("'"+name.getLiteral()+"' = '"+scope.getVariable(name.getLiteral()).get().getLiteral()+"'");
    }

    private static void checkForVariableAssignation(List<Token> tokens){
        Token first = tokens.get(0);

        if(tokens.size() < 2)
            return;

        if(!Token.VARIABLE_NAME.equals(first))
            return;

        if(!Token.EQUALS.equals(tokens.get(1))){
            System.out.println("Expected '='");
            return;
        }

        if(tokens.size() == 2){
            System.out.println("Expected an expression");
            return;
        }

        Optional<Token> varOpt = scope.getVariable(first.getLiteral());
        if(varOpt.isEmpty()){
            System.out.println("Variable '"+first.getLiteral()+"' does not exist");
            return;
        }

        assignVariable(first.getLiteral(), tokens.subList(2, tokens.size()), varOpt.get());
        System.out.println("'"+first.getLiteral()+"' = '"+scope.getVariable(first.getLiteral()).get().getLiteral()+"'");

    }

}
