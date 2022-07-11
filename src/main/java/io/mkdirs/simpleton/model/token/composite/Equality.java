package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class Equality extends Token {

    public Equality(){super("EQUALITY", "==");}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
