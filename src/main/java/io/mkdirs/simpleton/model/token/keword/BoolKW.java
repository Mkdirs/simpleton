package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class BoolKW extends Token {

    public BoolKW(){super("BOOL_KW", "bool");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
