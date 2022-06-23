package io.mkdirs.simpleton.evaluator;

import io.mkdirs.simpleton.evaluator.operator.Operand;
import io.mkdirs.simpleton.evaluator.operator.Operator;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.scope.ScopeContext;

import java.util.*;
import java.util.stream.Stream;

public class ExpressionEvaluator {
    private static final Operator[] OPERATORS = new Operator[]{
            new Operator(Token.PLUS, 3)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a, b) ->{

                        return Token.INTEGER_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) + Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a, b) ->{
                        return Token.FLOAT_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) + Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a, b) ->{
                        return Token.FLOAT_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) + Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a, b) ->{
                        return Token.FLOAT_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) + Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.STRING_LITERAL), Operand.with(Token.STRING_LITERAL), (a, b) ->{
                        return Token.STRING_LITERAL.with(a.getLiteral() + b.getLiteral());
                    }),






            new Operator(Token.MINUS, 3)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a, b) -> {
                        return Token.INTEGER_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) - Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.NOTHING, Operand.with(Token.INTEGER_LITERAL), (__,b) -> {
                        return Token.INTEGER_LITERAL.with(String.valueOf(-Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.FLOAT_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) - Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a, b) ->{
                        return Token.FLOAT_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) - Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a, b) ->{
                        return Token.FLOAT_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) - Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.NOTHING, Operand.with(Token.FLOAT_LITERAL), (__,b) -> {
                        return Token.FLOAT_LITERAL.with(String.valueOf(-Float.parseFloat(b.getLiteral())));
                    }),







            new Operator(Token.TIMES, 4)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.INTEGER_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) * Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.FLOAT_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) * Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.FLOAT_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) * Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.FLOAT_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) * Float.parseFloat(b.getLiteral())));
                    }),




            new Operator(Token.DIVIDE, 4)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.INTEGER_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) / Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.FLOAT_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) / Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.FLOAT_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) / Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.FLOAT_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) / Integer.parseInt(b.getLiteral())));
                    }),





            new Operator(Token.OR, 3)
                    .acceptTypes(Operand.with(Token.BOOLEAN_LITERAL), Operand.with(Token.BOOLEAN_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Boolean.parseBoolean(a.getLiteral()) || Boolean.parseBoolean(b.getLiteral())));
                    }),




            new Operator(Token.AND, 4)
                    .acceptTypes(Operand.with(Token.BOOLEAN_LITERAL), Operand.with(Token.BOOLEAN_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Boolean.parseBoolean(a.getLiteral()) && Boolean.parseBoolean(b.getLiteral())));
                    }),




            new Operator(Token.NOT, 6)
                    .acceptTypes(Operand.NOTHING, Operand.with(Token.BOOLEAN_LITERAL), (__,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(!Boolean.parseBoolean(b.getLiteral())));
                    }),




            new Operator(Token.EQUALITY, 1)
                    .acceptTypes(Operand.with(Token.BOOLEAN_LITERAL), Operand.with(Token.BOOLEAN_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Boolean.parseBoolean(a.getLiteral()) == Boolean.parseBoolean(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) == Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) == Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(a.getLiteral().charAt(0) == b.getLiteral().charAt(0)));
                    })

                    .acceptTypes(Operand.with(Token.STRING_LITERAL), Operand.with(Token.STRING_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(a.getLiteral().equals(b.getLiteral())));
                    }),







            new Operator(Token.INEQUALITY, 1)
                    .acceptTypes(Operand.with(Token.BOOLEAN_LITERAL), Operand.with(Token.BOOLEAN_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Boolean.parseBoolean(a.getLiteral()) != Boolean.parseBoolean(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) != Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) != Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(a.getLiteral().charAt(0) != b.getLiteral().charAt(0)));
                    })

                    .acceptTypes(Operand.with(Token.STRING_LITERAL), Operand.with(Token.STRING_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(!a.getLiteral().equals(b.getLiteral())));
                    }),





            new Operator(Token.GREATER_THAN, 5)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) > Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) > Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(a.getLiteral().charAt(0) > b.getLiteral().charAt(0)));
                    }),







            new Operator(Token.GREATER_THAN_EQUALS, 5)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) >= Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) >= Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(a.getLiteral().charAt(0) >= b.getLiteral().charAt(0)));
                    }),





            new Operator(Token.SMALLER_THAN, 5)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) < Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) < Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(a.getLiteral().charAt(0) < b.getLiteral().charAt(0)));
                    }),







            new Operator(Token.SMALLER_THAN_EQUALS, 5)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Integer.parseInt(a.getLiteral()) <= Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(Float.parseFloat(a.getLiteral()) <= Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return Token.BOOLEAN_LITERAL.with(String.valueOf(a.getLiteral().charAt(0) <= b.getLiteral().charAt(0)));
                    })
    };

    private final ScopeContext scopeContext;
    public ExpressionEvaluator(ScopeContext scopeContext){
        this.scopeContext = scopeContext;
    }

    public Optional<Token> evaluate(ASTNode tree){
        if(tree == null)
            return Optional.empty();

        if(tree.isLeaf())
            return Optional.of(tree.getToken());


        Token left = evaluate(tree.getLeft()).orElse(null);

        if(Token.VARIABLE_NAME.equals(left))
            left = this.scopeContext.getVariable(left.getLiteral()).orElse(null);

        Token right = evaluate(tree.getRight()).orElse(null);
        if(Token.VARIABLE_NAME.equals(right))
            right = this.scopeContext.getVariable(right.getLiteral()).orElse(null);


        Optional<Operator> operator = getOperator(tree.getToken());

        if(operator.isPresent()){
            return operator.get().evaluate(left, right);

        }else{
            //TODO
        }

        return Optional.empty();
    }

    private Optional<Operator> getOperator(Token token){
        return Arrays.stream(OPERATORS).filter(e  -> e.getToken().equals(token)).findFirst();
    }

    public Optional<ASTNode> buildTree(List<Token> tokens){

        if(tokens.isEmpty())
            return Optional.empty();
        else if(tokens.size() == 1)
            return Optional.of(new ASTNode(null, tokens.get(0), null));

        int closingParenthesis = getClosingParenthesis(0, tokens);

        if(closingParenthesis != -1 && closingParenthesis == tokens.size()-1)
            return buildTree(tokens.subList(1, closingParenthesis));

        int mainOperatorIndex = findMainOperatorIndex(tokens);

        if(mainOperatorIndex == -1)
            return Optional.empty();


        Token operator = tokens.get(mainOperatorIndex);
        Optional<ASTNode> left = buildTree(tokens.subList(0, mainOperatorIndex));
        Optional<ASTNode> right = buildTree(tokens.subList(mainOperatorIndex+1, tokens.size()));

        ASTNode tree = new ASTNode(left.orElse(null), operator, right.orElse(null));

        return Optional.of(tree);
    }

    private int getClosingParenthesis(int start, List<Token> tokens){
        if(!Token.LEFT_PARENTHESIS.equals(tokens.get(start)))
            return -1;

        int openParenthesis = 1;
        int i = start+1;
        while(i < tokens.size() && openParenthesis > 0){
            Token t = tokens.get(i);

            if(Token.LEFT_PARENTHESIS.equals(t))
                openParenthesis++;
            else if(Token.RIGHT_PARENTHESIS.equals(t))
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

            if(token.equals(Token.LEFT_PARENTHESIS)) {
                currentOperatorParenthesisScope++;
            }else if(token.equals(Token.RIGHT_PARENTHESIS)){
                currentOperatorParenthesisScope--;

                if(currentOperatorParenthesisScope < 1)
                    currentOperatorParenthesisScope = 1;
            }

            Optional<Operator> operator = Arrays.stream(OPERATORS).filter(e -> e.getToken().equals(token)).findFirst();
            if(operator.isPresent()){
                if(mainOperatorIndex == -1) {
                    mainOperatorIndex = i;
                    mainOperatorParenthesisScope = currentOperatorParenthesisScope;
                }else{
                    final int main = mainOperatorIndex;
                    Optional<Operator> mainOperatorOpt = Arrays.stream(OPERATORS).filter(e -> e.getToken().equals(tokens.get(main))).findFirst();

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
