package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;
import io.mkdirs.simpleton.model.token.literal.NullPlaceholder;

public class VariableHolder {

    private final Type type;
    private LiteralValueToken value;

    public VariableHolder(Type type, LiteralValueToken value){
        this.type = type;
        this.value = value;
    }

    public VariableHolder(Type type){this(type, NullPlaceholder.NULL);}


    public Type getType() {
        return type;
    }

    public LiteralValueToken getValue() {
        return value;
    }

    public void setValue(LiteralValueToken value) {
        this.value = value;
    }
}
