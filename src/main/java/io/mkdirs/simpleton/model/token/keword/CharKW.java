package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class CharKW extends Token {

    public CharKW(){super("CHAR_KW", "char");}


    @Override
    public boolean isKeyword() {
        return true;
    }

    @Override
    public String group() {
        return "TYPE";
    }
}
