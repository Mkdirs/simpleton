package io.mkdirs.simpleton.model.error;

import java.util.Stack;

public class ErrorStack {
    private ErrorStack(){}
    public static final ErrorStack STACK = new ErrorStack();
    private final Stack<StackableError> stack = new Stack<>();

    public void push(StackableError error){
        stack.push(error);
    }
    public StackableError pop(){
        return stack.pop();
    }
    public boolean isEmpty(){
        return stack.isEmpty();
    }
}
