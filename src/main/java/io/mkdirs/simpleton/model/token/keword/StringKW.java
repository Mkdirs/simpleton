package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class StringKW extends Token {

    public StringKW(){super("STRING_KW", "string");}

    @Override
    public boolean isKeyword() {
        return true;
    }

    @Override
    public String group() {
        return "TYPE";
    }
}
