package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.Value;

public interface IOperator {

    Value evaluate(Value left, Value right);
}
