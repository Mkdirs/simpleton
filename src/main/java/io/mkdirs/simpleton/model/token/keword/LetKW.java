package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class LetKW extends Token {

    public LetKW(){super("LET_KW", "let");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
