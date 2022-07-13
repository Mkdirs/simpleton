package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class VoidKW extends Token {

    public VoidKW(){super("VOID_KW", "void");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
