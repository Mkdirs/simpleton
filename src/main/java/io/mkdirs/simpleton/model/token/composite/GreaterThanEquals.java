package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class GreaterThanEquals extends Token {

    public GreaterThanEquals(){super("GREATER_THAN_EQUALS", ">=");}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
