package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.literal.BooleanLiteral;
import io.mkdirs.simpleton.model.token.literal.FloatLiteral;
import io.mkdirs.simpleton.model.token.literal.IntegerLiteral;
import io.mkdirs.simpleton.model.token.literal.StringLiteral;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Operator {
    //TODO: add null handling
    public static final Operator[] OPERATORS = new Operator[]{
            new Operator(Token.PLUS, 3)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a, b) ->{

                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) + Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) + Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) + Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) + Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.STRING_LITERAL), Operand.with(Token.STRING_LITERAL), (a, b) ->{
                        return new StringLiteral(a.getLiteral() + b.getLiteral());
            }),






            new Operator(Token.MINUS, 3)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a, b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) - Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.NOTHING, Operand.with(Token.INTEGER_LITERAL), (__,b) -> {
                        return new IntegerLiteral(String.valueOf(-Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) - Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) - Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) - Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.NOTHING, Operand.with(Token.FLOAT_LITERAL), (__,b) -> {
                        return new FloatLiteral(String.valueOf(-Float.parseFloat(b.getLiteral())));
            }),







            new Operator(Token.STAR, 4)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) * Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) * Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) * Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) * Float.parseFloat(b.getLiteral())));
            }),




            new Operator(Token.DIVIDE, 4)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) / Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) / Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) / Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) / Integer.parseInt(b.getLiteral())));
            }),





            new Operator(Token.OR, 3)
                    .acceptTypes(Operand.with(Token.BOOLEAN_LITERAL), Operand.with(Token.BOOLEAN_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.getLiteral()) || Boolean.parseBoolean(b.getLiteral())));
            }),




            new Operator(Token.AND, 4)
                    .acceptTypes(Operand.with(Token.BOOLEAN_LITERAL), Operand.with(Token.BOOLEAN_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.getLiteral()) && Boolean.parseBoolean(b.getLiteral())));
            }),




            new Operator(Token.NOT, 6)
                    .acceptTypes(Operand.NOTHING, Operand.with(Token.BOOLEAN_LITERAL), (__,b) -> {
                        return new BooleanLiteral(String.valueOf(!Boolean.parseBoolean(b.getLiteral())));
            }),




            new Operator(Token.EQUALITY, 1)
                    .acceptTypes(Operand.with(Token.BOOLEAN_LITERAL), Operand.with(Token.BOOLEAN_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.getLiteral()) == Boolean.parseBoolean(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) == Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) == Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.getLiteral().charAt(0) == b.getLiteral().charAt(0)));
                    })

                    .acceptTypes(Operand.with(Token.STRING_LITERAL), Operand.with(Token.STRING_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.getLiteral().equals(b.getLiteral())));
            }),







            new Operator(Token.INEQUALITY, 1)
                    .acceptTypes(Operand.with(Token.BOOLEAN_LITERAL), Operand.with(Token.BOOLEAN_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.getLiteral()) != Boolean.parseBoolean(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) != Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) != Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.getLiteral().charAt(0) != b.getLiteral().charAt(0)));
                    })

                    .acceptTypes(Operand.with(Token.STRING_LITERAL), Operand.with(Token.STRING_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(!a.getLiteral().equals(b.getLiteral())));
            }),





            new Operator(Token.GREATER_THAN, 5)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) > Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) > Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.getLiteral().charAt(0) > b.getLiteral().charAt(0)));
            }),







            new Operator(Token.GREATER_THAN_EQUALS, 5)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) >= Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) >= Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.getLiteral().charAt(0) >= b.getLiteral().charAt(0)));
            }),





            new Operator(Token.LOWER_THAN, 5)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) < Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) < Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.getLiteral().charAt(0) < b.getLiteral().charAt(0)));
            }),







            new Operator(Token.LOWER_THAN_EQUALS, 5)
                    .acceptTypes(Operand.with(Token.INTEGER_LITERAL), Operand.with(Token.INTEGER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.getLiteral()) <= Integer.parseInt(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.FLOAT_LITERAL), Operand.with(Token.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.getLiteral()) <= Float.parseFloat(b.getLiteral())));
                    })

                    .acceptTypes(Operand.with(Token.CHARACTER_LITERAL), Operand.with(Token.CHARACTER_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.getLiteral().charAt(0) <= b.getLiteral().charAt(0)));
            })
    };

    private final Token token;
    private final int priority;
    private final List<Context> contexts = new ArrayList<>();
    private static final String NULL = Token.NULL_KW.getLiteral();

    public Operator(Token token, int priority){
        this.token = token;
        this.priority = priority;
    }

    public Token getToken() {
        return token;
    }

    public int getPriority() {
        return priority;
    }


    private Operator acceptTypes(Operand left, Operand right, IOperator handler){
        this.contexts.add(new Context(left, right, handler));
        return this;
    }



    public Optional<Token> evaluate(Token left, Token right){
        Optional<Context> ctx = this.contexts.stream().filter(e -> e.getLeft().match(left) && e.getRight().match(right)).findFirst();

        if(ctx.isEmpty())
            return Optional.empty();

        return Optional.of(ctx.get().evaluate(left, right));
    }

}

class Context{
    private final Operand left;
    private final Operand right;

    private final IOperator handler;

    public Context(Operand left, Operand right, IOperator handler){
        this.left = left;
        this.right = right;
        this.handler = handler;
    }

    public Operand getLeft() {
        return left;
    }

    public Operand getRight() {
        return right;
    }

    public Token evaluate(Token l, Token r){
        return this.handler.evaluate(l, r);
    }
}

