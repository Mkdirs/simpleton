package io.mkdirs.simpleton.func_executor;

import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;
import io.mkdirs.simpleton.result.Result;

public interface IFuncExecutor {

    public Result<LiteralValueToken> execute(Func func);
}
