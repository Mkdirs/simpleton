package io.mkdirs.simpleton.func_executor;

import io.mkdirs.simpleton.model.Value;
import io.mkdirs.simpleton.model.error.StackableError;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.result.Result;

public interface IFuncExecutor {

    Result<Value, StackableError> execute(Func func);
}
