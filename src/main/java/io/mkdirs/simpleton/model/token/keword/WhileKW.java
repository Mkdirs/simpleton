package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class WhileKW extends Token {

    public WhileKW(){super("WHILE_KW", "while");}

    @Override
    public boolean isKeyword() {
        return true;
    }
}
