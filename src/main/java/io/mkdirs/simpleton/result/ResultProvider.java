package io.mkdirs.simpleton.result;

import io.mkdirs.simpleton.scope.ScopeContext;

public abstract class ResultProvider {

    protected String statement;
    protected Result result;


    public void setStatement(String statement){this.statement = statement;}

    public Result pushError(String message, int start, int length){
        StringBuilder builder = new StringBuilder()
                .append(message)
                .append("\n")
                .append("\t").append(this.statement)
                .append("\n\t")
                .append(" ".repeat(start)).append("^".repeat(length));

        return Result.failure(builder.toString());
    }

    public Result pushError(String message, int start){return pushError(message, start, this.statement.length());}

    public Result pushError(String message){return pushError(message, 0);}
}
