package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class Inequality extends Token {

    public Inequality(){super("INEQUALITY", "!=");}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
