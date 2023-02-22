package io.mkdirs.simpleton.model.error;

import java.util.Stack;

public class ErrorStack {
    private final Stack<StackableError> errors = new Stack<>();

    public void push(StackableError error){
        errors.push(error);
    }
}
