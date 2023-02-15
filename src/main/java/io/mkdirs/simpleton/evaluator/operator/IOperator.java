package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;

public interface IOperator {

    LiteralValueToken evaluate(LiteralValueToken left, LiteralValueToken right);
}
