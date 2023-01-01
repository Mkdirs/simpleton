package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class ElseKW extends Token {

    public ElseKW(){super("ELSE_KW", "else");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
