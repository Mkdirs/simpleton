package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ScopeContext {

    private final ScopeContext parent;
    private final HashMap<String, Token> variables = new HashMap<>();

    private String line;
    private ScopeContext subScope;

    public ScopeContext(ScopeContext parent){
        this.parent = parent;
    }

    public ScopeContext(){this(null);}

    public String getLine(){return this.line;}

    public void setLine(String line) {
        this.line = line;
    }


    public void pushVariable(String name, Token type, String value){
        this.variables.put(name, type.with(value));
    }

    public void pushVariable(String name, Token type){
        pushVariable(name, type, Token.NULL_KW.getLiteral());
    }

    public Optional<Token> getVariable(String name){
        if(this.variables.containsKey(name))
            return Optional.of(this.variables.get(name));

        if(this.parent != null)
            return this.parent.getVariable(name);

        return Optional.empty();
    }

}
