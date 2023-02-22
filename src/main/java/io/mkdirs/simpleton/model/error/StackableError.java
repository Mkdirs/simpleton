package io.mkdirs.simpleton.model.error;

import java.util.ArrayList;
import java.util.List;

public record StackableError(String message, String statement, Cursor[] cursors) {

    public String highlightError(){
        StringBuilder builder = new StringBuilder(message)
                .append("\n").append(statement);

        for(Cursor cursor : cursors){
            builder.append(" ".repeat(cursor.start()))
                    .append("^".repeat(cursor.length()));
        }

        return builder.toString();
    }
}

record Cursor(int start, int length){}
