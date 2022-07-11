package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.token.Token;

public class VariableHolder {

    private final Token type;
    private Token value;

    public VariableHolder(Token type, Token value){
        this.type = type;
        this.value = value;
    }

    public VariableHolder(Token type){this(type, Token.NULL_KW);}


    public Token getType() {
        return type;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }
}
