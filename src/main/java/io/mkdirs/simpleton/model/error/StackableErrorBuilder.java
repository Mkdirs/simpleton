package io.mkdirs.simpleton.model.error;

import java.util.ArrayList;
import java.util.List;

public class StackableErrorBuilder{
    private final String message;
    private String statement = "";
    private final List<Cursor> cursors = new ArrayList<>();

    public StackableErrorBuilder(String message){
        this.message = message;
    }

    public StackableErrorBuilder withStatement(String statement){
        this.statement = statement;
        return  this;
    }

    public StackableErrorBuilder withCursor(int start, int length){
        cursors.add(new Cursor(start, length));
        return this;
    }

    public StackableErrorBuilder withCursor(int start){
        cursors.add(new Cursor(start, statement.length()-start));
        return this;
    }

    public StackableError build(){
        return new StackableError(message, statement, cursors.toArray(Cursor[]::new));
    }

}
