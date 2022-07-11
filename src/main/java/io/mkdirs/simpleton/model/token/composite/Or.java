package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class Or extends Token {

    public Or(){super("OR", "||");}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
