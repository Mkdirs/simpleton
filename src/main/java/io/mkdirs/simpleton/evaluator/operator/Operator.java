package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.model.token.literal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Operator {
    public static final Operator[] OPERATORS = new Operator[]{
            new Operator(TokenKind.PLUS, 3)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a, b) ->{

                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) + Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) + Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) + Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.value) + Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.STRING_LITERAL), Operand.with(TokenKind.STRING_LITERAL), (a, b) ->{
                        return new StringLiteral(a.value + b.value);
            }),






            new Operator(TokenKind.MINUS, 3)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a, b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) - Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.NOTHING, Operand.with(TokenKind.INT_LITERAL), (__,b) -> {
                        return new IntegerLiteral(String.valueOf(-Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) - Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) - Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.value) - Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.NOTHING, Operand.with(TokenKind.FLOAT_LITERAL), (__,b) -> {
                        return new FloatLiteral(String.valueOf(-Float.parseFloat(b.value)));
            }),







            new Operator(TokenKind.STAR, 4)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) * Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) * Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) * Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.value) * Float.parseFloat(b.value)));
            }),




            new Operator(TokenKind.DIVIDE, 4)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) / Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) / Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.value) / Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) / Integer.parseInt(b.value)));
            }),





            new Operator(TokenKind.PERCENT, 4)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a, b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) % Integer.parseInt(b.value)));
            }),





            new Operator(TokenKind.OR, 3)
                    .acceptTypes(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.BOOL_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.value) || Boolean.parseBoolean(b.value)));
            }),




            new Operator(TokenKind.AND, 4)
                    .acceptTypes(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.BOOL_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.value) && Boolean.parseBoolean(b.value)));
            }),




            new Operator(TokenKind.NOT, 6)
                    .acceptTypes(Operand.NOTHING, Operand.with(TokenKind.BOOL_LITERAL), (__,b) -> {
                        return new BooleanLiteral(String.valueOf(!Boolean.parseBoolean(b.value)));
            }),




            new Operator(TokenKind.EQUALITY, 1)
                    .acceptTypes(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.BOOL_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.value) == Boolean.parseBoolean(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) == Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) == Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) == b.value.charAt(0)));
                    })

                    .acceptTypes(Operand.with(TokenKind.STRING_LITERAL), Operand.with(TokenKind.STRING_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.equals(b.value)));
            }),







            new Operator(TokenKind.INEQUALITY, 1)
                    .acceptTypes(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.BOOL_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.value) != Boolean.parseBoolean(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) != Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) != Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) != b.value.charAt(0)));
                    })

                    .acceptTypes(Operand.with(TokenKind.STRING_LITERAL), Operand.with(TokenKind.STRING_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(!a.value.equals(b.value)));
            }),





            new Operator(TokenKind.GREATER_THAN, 5)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) > Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) > Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) > b.value.charAt(0)));
            }),







            new Operator(TokenKind.GREATER_THAN_EQ, 5)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) >= Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) >= Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) >= b.value.charAt(0)));
            }),





            new Operator(TokenKind.LOWER_THAN, 5)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) < Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) < Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) < b.value.charAt(0)));
            }),







            new Operator(TokenKind.LOWER_THAN_EQ, 5)
                    .acceptTypes(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) <= Integer.parseInt(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) <= Float.parseFloat(b.value)));
                    })

                    .acceptTypes(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) <= b.value.charAt(0)));
            })
    };

    private final TokenKind tokenKind;
    private final int priority;
    private final List<Context> contexts = new ArrayList<>();
    private static final String NULL = TokenKind.NULL_KW.literal;

    public Operator(TokenKind tokenKind, int priority){
        this.tokenKind = tokenKind;
        this.priority = priority;
    }

    public TokenKind getTokenKind() {
        return tokenKind;
    }

    public int getPriority() {
        return priority;
    }


    private Operator acceptTypes(Operand left, Operand right, IOperator handler){
        this.contexts.add(new Context(left, right, handler));
        return this;
    }



    public Optional<LiteralValueToken> evaluate(LiteralValueToken left, LiteralValueToken right){
        TokenKind l = left == null ? null : left.kind;
        TokenKind r = right == null ? null : right.kind;
        Optional<Context> ctx = this.contexts.stream().filter(e -> e.getLeft().match(l) && e.getRight().match(r)).findFirst();

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

    public LiteralValueToken evaluate(LiteralValueToken l, LiteralValueToken r){
        return this.handler.evaluate(l, r);
    }
}

