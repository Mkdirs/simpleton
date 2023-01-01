package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class DoKW extends Token {

    public DoKW(){super("DO_KW", "do");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
