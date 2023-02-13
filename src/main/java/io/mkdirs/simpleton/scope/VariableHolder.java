package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.Token;

public class VariableHolder {

    private final Type type;
    private Token value;

    public VariableHolder(Type type, Token value){
        this.type = type;
        this.value = value;
    }

    public VariableHolder(Type type){this(type, Token.NULL_KW);}


    public Type getType() {
        return type;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }
}
