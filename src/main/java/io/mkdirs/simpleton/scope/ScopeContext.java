package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ScopeContext {

    private final ScopeContext parent;
    private final HashMap<String, Token> variables = new HashMap<>();
    private final List<String> errorStack = new ArrayList<>();

    private String line;
    private ScopeContext subScope;

    public ScopeContext(ScopeContext parent, String line){
        this.parent = parent;
        this.line = line;
    }

    public ScopeContext(String line){this(null, line);}

    public String getLine(){return this.line;}

    public void setLine(String line) {
        this.line = line;
    }

    public void pushError(String message, int start, int length){
        StringBuilder builder = new StringBuilder()
                .append(message)
                .append("\n")
                .append("\t").append(this.line)
                .append("\n\t")
                .append(" ".repeat(start)).append("^".repeat(length));

        this.errorStack.add(0, builder.toString());
    }

    public boolean hasErrors(){return !this.errorStack.isEmpty();}
    public List<String> getErrorStack(){return this.errorStack;}

    public void pushVariable(String name, Token value){
        this.variables.put(name, value);
    }

    public void pushVariable(String name){
        this.variables.put(name, null);
    }

    public Optional<Token> getVariable(String name){
        if(this.variables.containsKey(name))
            return Optional.of(this.variables.get(name));

        if(this.parent != null)
            return this.parent.getVariable(name);

        return Optional.empty();
    }

}
