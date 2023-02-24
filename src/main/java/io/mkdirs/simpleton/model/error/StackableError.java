package io.mkdirs.simpleton.model.error;

import java.util.ArrayList;
import java.util.List;

public record StackableError(String message, String statement, int line, Cursor[] cursors) {

    public String highlightError(){
        StringBuilder builder = new StringBuilder();
        builder.append("Error");
        if(line != -1)
            builder.append(" at line "+(line+1));

        builder.append("\n\t");

        builder.append(message).append("\n\t").append(statement).append("\n\t");

        for(Cursor cursor : cursors){
            builder.append(" ".repeat(cursor.start()))
                    .append("^".repeat(cursor.length()));
        }

        return builder.toString();
    }
}

record Cursor(int start, int length){}
