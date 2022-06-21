package io.mkdirs.simpleton.evaluator;

import io.mkdirs.simpleton.model.token.Token;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExpressionEvaluator {
    private static final Map<Token, Integer> OPERATORS_PRIORITY = Map.ofEntries(
            Map.entry(Token.PLUS, 1),
            Map.entry(Token.MINUS, 1),
            Map.entry(Token.TIMES, 2),
            Map.entry(Token.DIVIDE, 2),

            Map.entry(Token.OR, 1),
            Map.entry(Token.AND, 2),
            Map.entry(Token.NOT, 3),

            Map.entry(Token.EQUALITY, 1),
            Map.entry(Token.INEQUALITY, 1),

            Map.entry(Token.GREATER_THAN, 1),
            Map.entry(Token.GREATER_THAN_EQUALS, 1),

            Map.entry(Token.SMALLER_THAN, 1),
            Map.entry(Token.SMALLER_THAN_EQUALS, 1)
    );

    public void evaluate(ASTNode tree){

    }

    public Optional<ASTNode> buildTree(List<Token> tokens){

        if(tokens.isEmpty())
            return Optional.empty();
        else if(tokens.size() == 1)
            return Optional.of(new ASTNode(null, tokens.get(0), null));

        boolean skipParenthesis = (tokens.get(0).equals(Token.LEFT_PARENTHESIS) && tokens.get(tokens.size()-1).equals(Token.RIGHT_PARENTHESIS));

        if(skipParenthesis)
            return buildTree(tokens.subList(1, tokens.size()-1));

        int mainOperatorIndex = findMainOperatorIndex(tokens);

        if(mainOperatorIndex == -1)
            return Optional.empty();


        Token operator = tokens.get(mainOperatorIndex);
        Optional<ASTNode> left = buildTree(tokens.subList(0, mainOperatorIndex));
        Optional<ASTNode> right = buildTree(tokens.subList(mainOperatorIndex+1, tokens.size()));

        ASTNode tree = new ASTNode(left.orElse(null), operator, right.orElse(null));

        return Optional.of(tree);
    }

    private int findMainOperatorIndex(List<Token> tokens){
        int mainOperatorIndex = -1;
        int currentOperatorParenthesisScope = 1;
        int mainOperatorParenthesisScope = 1;
        final int parenthesisFactor = 5;

        for(int i = 0; i < tokens.size(); i++){
            Token token = tokens.get(i);

            if(token.equals(Token.LEFT_PARENTHESIS)) {
                currentOperatorParenthesisScope++;
            }else if(token.equals(Token.RIGHT_PARENTHESIS)){
                currentOperatorParenthesisScope--;

                if(currentOperatorParenthesisScope < 1)
                    currentOperatorParenthesisScope = 1;
            }


            if(OPERATORS_PRIORITY.containsKey(token)){
                if(mainOperatorIndex == -1) {
                    mainOperatorIndex = i;
                    mainOperatorParenthesisScope = currentOperatorParenthesisScope;
                }else if(OPERATORS_PRIORITY.get(token) + (currentOperatorParenthesisScope*parenthesisFactor) < OPERATORS_PRIORITY.get(tokens.get(mainOperatorIndex)) + (mainOperatorParenthesisScope*parenthesisFactor) ){
                    mainOperatorIndex = i;
                    mainOperatorParenthesisScope = currentOperatorParenthesisScope;
                }
            }

        }

        return mainOperatorIndex;
    }


}
