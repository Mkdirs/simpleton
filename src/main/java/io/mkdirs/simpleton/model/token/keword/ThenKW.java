package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class ThenKW extends Token {

    public ThenKW(){super("THEN_KW", "then");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
