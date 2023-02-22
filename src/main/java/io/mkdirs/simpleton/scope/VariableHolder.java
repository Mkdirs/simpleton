package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.Value;
import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;

public class VariableHolder {

    private final Type type;
    private Value value;

    public VariableHolder(Type type, Value value){
        this.type = type;
        this.value = value;
    }

    public VariableHolder(Type type){this(type, Value.NULL);}


    public Type getType() {
        return type;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
