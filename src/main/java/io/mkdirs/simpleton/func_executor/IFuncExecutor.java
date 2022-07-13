package io.mkdirs.simpleton.func_executor;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.result.Result;

public interface IFuncExecutor {

    public Result<Token> execute(Func func);
}
