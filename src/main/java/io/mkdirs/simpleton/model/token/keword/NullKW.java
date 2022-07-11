package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class NullKW extends Token {

    public NullKW(){super("NULL_KW", "null");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
