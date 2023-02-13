package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class ReturnKW extends Token {

    public ReturnKW(){super("RETURN_KW", "return");}

    @Override
    public boolean isKeyword() {
        return true;
    }

}
