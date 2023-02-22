package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.Value;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.model.token.literal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Operator {
    public static final Operator[] OPERATORS = new Operator[]{
            new OperatorBuild(TokenKind.PLUS, 3)
                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a, b) ->{
                        return new Value(Type.INTEGER, String.valueOf(Integer.parseInt(a.value()) + Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a, b) ->{
                        return new Value(Type.FLOAT, String.valueOf(Float.parseFloat(a.value()) + Float.parseFloat(b.value())));
                    })

                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.FLOAT), (a, b) -> {
                        return new Value(Type.FLOAT, String.valueOf(Float.parseFloat(a.value()) + Float.parseFloat(b.value())));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.STRING), Operand.with(Type.STRING), (a, b) ->{
                        return new Value(Type.STRING,a.value() + b.value());
                    })
                    .build(),





            new OperatorBuild(TokenKind.MINUS, 3)
                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a, b) ->{
                        return new Value(Type.INTEGER, String.valueOf(Integer.parseInt(a.value()) - Integer.parseInt(b.value())));
                    })

                    .acceptUnaryLeft(Operand.with(Type.INTEGER), (__, b) ->{
                        return new Value(Type.INTEGER, String.valueOf(-Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a, b) ->{
                        return new Value(Type.FLOAT, String.valueOf(Float.parseFloat(a.value()) - Float.parseFloat(b.value())));
                    })

                    .acceptUnaryLeft(Operand.with(Type.FLOAT), (__, b) ->{
                        return new Value(Type.FLOAT, String.valueOf(-Float.parseFloat(b.value())));
                    })


                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.FLOAT), (a, b) ->{
                        return new Value(Type.FLOAT, String.valueOf(Integer.parseInt(a.value()) - Float.parseFloat(b.value())));
                    })
                    .withTypeCommutation((a, b) ->{
                        return new Value(Type.FLOAT, String.valueOf(Float.parseFloat(a.value()) - Integer.parseInt(b.value())));
                    })

                    .build(),





            new OperatorBuild(TokenKind.STAR, 4)
                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a,b) -> {
                        return new Value(Type.INTEGER, String.valueOf(Integer.parseInt(a.value()) * Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a,b) -> {
                        return new Value(Type.FLOAT, String.valueOf(Float.parseFloat(a.value()) * Float.parseFloat(b.value())));
                    })

                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.FLOAT), (a, b) -> {
                        return new Value(Type.FLOAT, String.valueOf(Integer.parseInt(a.value()) * Float.parseFloat(b.value())));
                    })
                    .withCommutation()

                    .build(),



            new OperatorBuild(TokenKind.DIVIDE, 4)
                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a,b) -> {
                        return new Value(Type.INTEGER, String.valueOf(Integer.parseInt(a.value()) / Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a,b) -> {
                        return new Value(Type.FLOAT, String.valueOf(Float.parseFloat(a.value()) / Float.parseFloat(b.value())));
                    })

                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.FLOAT), (a, b) ->{
                        return new Value(Type.FLOAT, String.valueOf(Integer.parseInt(a.value()) / Float.parseFloat(b.value())));
                    })
                    .withTypeCommutation((a,b) -> {
                        return new Value(Type.FLOAT, String.valueOf(Float.parseFloat(a.value()) / Integer.parseInt(b.value())));
                    })

                    .build(),




            new OperatorBuild(TokenKind.PERCENT, 4)
                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a, b) ->{
                        return new Value(Type.INTEGER, String.valueOf(Integer.parseInt(a.value()) % Integer.parseInt(b.value())));
                    })
                    .build(),




            new OperatorBuild(TokenKind.OR, 3)
                    .accept(Operand.with(Type.BOOLEAN), Operand.with(Type.BOOLEAN), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Boolean.parseBoolean(a.value()) || Boolean.parseBoolean(b.value())));
                    })
                    .build(),



            new OperatorBuild(TokenKind.AND, 4)
                    .accept(Operand.with(Type.BOOLEAN), Operand.with(Type.BOOLEAN), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Boolean.parseBoolean(a.value()) && Boolean.parseBoolean(b.value())));
                    })
                    .build(),



            new OperatorBuild(TokenKind.NOT, 6)
                    .acceptUnaryLeft(Operand.with(Type.BOOLEAN), (__,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(!Boolean.parseBoolean(b.value())));
                    })
                    .build(),



            new OperatorBuild(TokenKind.EQUALITY, 1)
                    .accept(Operand.with(Type.BOOLEAN), Operand.with(Type.BOOLEAN), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Boolean.parseBoolean(a.value()) == Boolean.parseBoolean(b.value())));
                    })

                    .accept(Operand.with(Type.BOOLEAN), Operand.with(Type.NULL), (a, b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Integer.parseInt(a.value()) == Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Float.parseFloat(a.value()) == Float.parseFloat(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.CHARACTER), Operand.with(Type.CHARACTER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(a.value().charAt(0) == b.value().charAt(0)));
                    })

                    .accept(Operand.with(Type.CHARACTER), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.STRING), Operand.with(Type.STRING), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(a.value().equals(b.value())));
                    })

                    .accept(Operand.with(Type.STRING), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(false));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.NULL), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(true));
                    })

                    .build(),







            new OperatorBuild(TokenKind.INEQUALITY, 1)
                    .accept(Operand.with(Type.BOOLEAN), Operand.with(Type.BOOLEAN), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Boolean.parseBoolean(a.value()) != Boolean.parseBoolean(b.value())));
                    })

                    .accept(Operand.with(Type.BOOLEAN), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Integer.parseInt(a.value()) != Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Float.parseFloat(a.value()) != Float.parseFloat(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.CHARACTER), Operand.with(Type.CHARACTER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(a.value().charAt(0) != b.value().charAt(0)));
                    })

                    .accept(Operand.with(Type.CHARACTER), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.STRING), Operand.with(Type.STRING), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(!a.value().equals(b.value())));
                    })

                    .accept(Operand.with(Type.STRING), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(true));
                    })
                    .withCommutation()

                    .accept(Operand.with(Type.NULL), Operand.with(Type.NULL), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(false));
                    })

                    .build(),





            new Operator(TokenKind.GREATER_THAN, 5)
                    .acceptTypes(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Integer.parseInt(a.value()) > Integer.parseInt(b.value())));
                    })

                    .acceptTypes(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Float.parseFloat(a.value()) > Float.parseFloat(b.value())));
                    })

                    .acceptTypes(Operand.with(Type.CHARACTER), Operand.with(Type.CHARACTER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(a.value().charAt(0) > b.value().charAt(0)));
            }),







            new OperatorBuild(TokenKind.GREATER_THAN_EQ, 5)
                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Integer.parseInt(a.value()) >= Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Float.parseFloat(a.value()) >= Float.parseFloat(b.value())));
                    })

                    .accept(Operand.with(Type.CHARACTER), Operand.with(Type.CHARACTER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(a.value().charAt(0) >= b.value().charAt(0)));
                    })

                    .build(),





            new OperatorBuild(TokenKind.LOWER_THAN, 5)
                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Integer.parseInt(a.value()) < Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Float.parseFloat(a.value()) < Float.parseFloat(b.value())));
                    })

                    .accept(Operand.with(Type.CHARACTER), Operand.with(Type.CHARACTER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(a.value().charAt(0) < b.value().charAt(0)));
                    })

                    .build(),







            new OperatorBuild(TokenKind.LOWER_THAN_EQ, 5)
                    .accept(Operand.with(Type.INTEGER), Operand.with(Type.INTEGER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Integer.parseInt(a.value()) <= Integer.parseInt(b.value())));
                    })

                    .accept(Operand.with(Type.FLOAT), Operand.with(Type.FLOAT), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(Float.parseFloat(a.value()) <= Float.parseFloat(b.value())));
                    })

                    .accept(Operand.with(Type.CHARACTER), Operand.with(Type.CHARACTER), (a,b) -> {
                        return new Value(Type.BOOLEAN, String.valueOf(a.value().charAt(0) <= b.value().charAt(0)));
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


    public Optional<Value> evaluate(Value left, Value right){
        Optional<Context> ctxOpt = this.contexts.stream().filter(e -> e.getLeft().match(left) && e.getRight().match(right)).findFirst();

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

    public Value evaluate(Value l, Value r){
        return this.handler.evaluate(l, r);
    }
}

