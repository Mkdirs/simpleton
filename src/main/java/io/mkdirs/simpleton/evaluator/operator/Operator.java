package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Operator {

    private final Token token;
    private final int priority;
    private final List<Context> contexts = new ArrayList<>();

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


    public Operator acceptTypes(Operand left, Operand right, IOperator handler){
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

