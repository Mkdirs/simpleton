package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class FloatKW extends Token {

    public FloatKW(){super("FLOAT_KW", "float");}

    @Override
    public boolean isKeyword() {
        return true;
    }

    @Override
    public String group() {
        return "TYPE";
    }
}
