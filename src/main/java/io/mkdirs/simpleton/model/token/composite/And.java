package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class And extends Token {

    public And(){super("AND", "&&");}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
