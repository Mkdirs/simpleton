package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.token.Token;

public interface IOperator {

    Token evaluate(Token left,Token right);
}
