package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.model.token.literal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Operator {
    public static final Operator[] OPERATORS = new Operator[]{
            new OperatorBuild(TokenKind.PLUS, 3)
                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a, b) ->{
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) + Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) + Float.parseFloat(b.value)));
                    })

                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) + Float.parseFloat(b.value)));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.STRING_LITERAL), Operand.with(TokenKind.STRING_LITERAL), (a, b) ->{
                        return new StringLiteral(a.value + b.value);
                    })
                    .build(),





            new OperatorBuild(TokenKind.MINUS, 3)
                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a, b) ->{
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) - Integer.parseInt(b.value)));
                    })

                    .acceptUnaryLeft(Operand.with(TokenKind.INT_LITERAL), (__, b) ->{
                        return new IntegerLiteral(String.valueOf(-Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) - Float.parseFloat(b.value)));
                    })

                    .acceptUnaryLeft(Operand.with(TokenKind.FLOAT_LITERAL), (__, b) ->{
                        return new FloatLiteral(String.valueOf(-Float.parseFloat(b.value)));
                    })


                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.value) - Float.parseFloat(b.value)));
                    })
                    .withTypeCommutation((a, b) ->{
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) - Integer.parseInt(b.value)));
                    })

                    .build(),





            new OperatorBuild(TokenKind.STAR, 4)
                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) * Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) * Float.parseFloat(b.value)));
                    })

                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) -> {
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.value) * Float.parseFloat(b.value)));
                    })
                    .withCommutation()

                    .build(),



            new OperatorBuild(TokenKind.DIVIDE, 4)
                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) / Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) / Float.parseFloat(b.value)));
                    })

                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a, b) ->{
                        return new FloatLiteral(String.valueOf(Integer.parseInt(a.value) / Float.parseFloat(b.value)));
                    })
                    .withTypeCommutation((a,b) -> {
                        return new FloatLiteral(String.valueOf(Float.parseFloat(a.value) / Integer.parseInt(b.value)));
                    })

                    .build(),




            new OperatorBuild(TokenKind.PERCENT, 4)
                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a, b) ->{
                        return new IntegerLiteral(String.valueOf(Integer.parseInt(a.value) % Integer.parseInt(b.value)));
                    })
                    .build(),




            new OperatorBuild(TokenKind.OR, 3)
                    .accept(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.BOOL_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.value) || Boolean.parseBoolean(b.value)));
                    })
                    .build(),



            new OperatorBuild(TokenKind.AND, 4)
                    .accept(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.BOOL_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.value) && Boolean.parseBoolean(b.value)));
                    })
                    .build(),



            new OperatorBuild(TokenKind.NOT, 6)
                    .acceptUnaryLeft(Operand.with(TokenKind.BOOL_LITERAL), (__,b) -> {
                        return new BooleanLiteral(String.valueOf(!Boolean.parseBoolean(b.value)));
                    })
                    .build(),



            new OperatorBuild(TokenKind.EQUALITY, 1)
                    .accept(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.BOOL_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.value) == Boolean.parseBoolean(b.value)));
                    })

                    .accept(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.NULL_KW), (a, b) -> {
                        return new BooleanLiteral(String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) == Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) == Float.parseFloat(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) == b.value.charAt(0)));
                    })

                    .accept(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.STRING_LITERAL), Operand.with(TokenKind.STRING_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.equals(b.value)));
                    })

                    .accept(Operand.with(TokenKind.STRING_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.NULL_KW), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(true));
                    })

                    .build(),







            new OperatorBuild(TokenKind.INEQUALITY, 1)
                    .accept(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.BOOL_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Boolean.parseBoolean(a.value) != Boolean.parseBoolean(b.value)));
                    })

                    .accept(Operand.with(TokenKind.BOOL_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) != Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) != Float.parseFloat(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) != b.value.charAt(0)));
                    })

                    .accept(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.STRING_LITERAL), Operand.with(TokenKind.STRING_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(!a.value.equals(b.value)));
                    })

                    .accept(Operand.with(TokenKind.STRING_LITERAL), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(TokenKind.NULL_KW), Operand.with(TokenKind.NULL_KW), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(false));
                    })

                    .build(),





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







            new OperatorBuild(TokenKind.GREATER_THAN_EQ, 5)
                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) >= Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) >= Float.parseFloat(b.value)));
                    })

                    .accept(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) >= b.value.charAt(0)));
                    })

                    .build(),





            new OperatorBuild(TokenKind.LOWER_THAN, 5)
                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) < Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) < Float.parseFloat(b.value)));
                    })

                    .accept(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) < b.value.charAt(0)));
                    })

                    .build(),







            new OperatorBuild(TokenKind.LOWER_THAN_EQ, 5)
                    .accept(Operand.with(TokenKind.INT_LITERAL), Operand.with(TokenKind.INT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Integer.parseInt(a.value) <= Integer.parseInt(b.value)));
                    })

                    .accept(Operand.with(TokenKind.FLOAT_LITERAL), Operand.with(TokenKind.FLOAT_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(Float.parseFloat(a.value) <= Float.parseFloat(b.value)));
                    })

                    .accept(Operand.with(TokenKind.CHAR_LITERAL), Operand.with(TokenKind.CHAR_LITERAL), (a,b) -> {
                        return new BooleanLiteral(String.valueOf(a.value.charAt(0) <= b.value.charAt(0)));
                    })

                    .build()
    };

    private final TokenKind tokenKind;
    private final int priority;
    protected final List<Context> contexts = new ArrayList<>();
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


    public Optional<LiteralValueToken> evaluate(LiteralValueToken left, LiteralValueToken right){
        TokenKind l = left == null ? null : left.kind;
        TokenKind r = right == null ? null : right.kind;
        Optional<Context> ctxOpt = this.contexts.stream().filter(e -> e.getLeft().match(l) && e.getRight().match(r)).findFirst();

        if(ctxOpt.isEmpty())
            return Optional.empty();

        Context ctx = ctxOpt.get();

        if(ctx.reversed)
            return Optional.of(ctx.evaluate(right, left));

        return Optional.of(ctx.evaluate(left, right));
    }

    private Operator acceptTypes(Operand left, Operand right, IOperator handler){
        this.contexts.add(new Context(left, right, handler));
        return this;
    }



}

class OperatorBuild{
    private TokenKind kind;
    private int priority;
    private List<Context> contexts = new ArrayList<>();
    public OperatorBuild(TokenKind kind, int priority){
        this.kind = kind;
        this.priority = priority;
    }
    public OperatorBuild accept(Operand left, Operand right, IOperator handler){
        contexts.add(new Context(left, right, handler));

        return this;
    }

    public OperatorBuild acceptUnaryLeft(Operand right, IOperator handler){
        contexts.add(new Context(Operand.NOTHING, right, handler));
        return this;
    }

    public OperatorBuild acceptUnaryRight(Operand left, IOperator handler){
        contexts.add(new Context(left, Operand.NOTHING, handler));
        return this;
    }

    public OperatorBuild withCommutation(){
        Context actual = contexts.get(contexts.size()-1);
        Context reversed = new Context(actual.getRight(), actual.getLeft(), actual.handler);
        reversed.reversed = true;

        contexts.add(reversed);
        return this;
    }

    public OperatorBuild withTypeCommutation(IOperator handler){
        Context actual = contexts.get(contexts.size()-1);
        contexts.add(new Context(actual.getRight(), actual.getLeft(), handler));

        return this;
    }


    public Operator build(){
        Operator op = new Operator(kind, priority);
        op.contexts.addAll(contexts);

        return op;
    }
}

class Context{
    private final Operand left;
    private final Operand right;

    protected final IOperator handler;
    protected boolean reversed = false;

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

