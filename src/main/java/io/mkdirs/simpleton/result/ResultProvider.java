package io.mkdirs.simpleton.result;

import io.mkdirs.simpleton.scope.ScopeContext;

public abstract class ResultProvider {

    protected ScopeContext scopeContext;
    protected Result result;

    protected ResultProvider(ScopeContext scopeContext){
        this.scopeContext = scopeContext;
    }

    public Result pushError(String message, int start, int length){
        StringBuilder builder = new StringBuilder()
                .append(message)
                .append("\n")
                .append("\t").append(this.scopeContext.getLine())
                .append("\n\t")
                .append(" ".repeat(start)).append("^".repeat(length));

        return Result.failure(builder.toString());
    }

    public Result pushError(String message, int start){return pushError(message, start, this.scopeContext.getLine().length());}

    public Result pushError(String message){return pushError(message, 0);}
}
