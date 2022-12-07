package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class IntKW extends Token {

    public IntKW(){super("INT_KW", "int");}

    @Override
    public boolean isKeyword() {
        return true;
    }

    @Override
    public String group() {
        return "TYPE";
    }
}
