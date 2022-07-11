package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class IfKW extends Token {

    public IfKW(){super("IF_KW", "if");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
