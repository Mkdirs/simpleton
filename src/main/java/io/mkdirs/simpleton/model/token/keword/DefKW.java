package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class DefKW extends Token {

    public DefKW(){super("DEF_KW", "def");}

    @Override
    public boolean isKeyword() {
        return true;
    }

}
