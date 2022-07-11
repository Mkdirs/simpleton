package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class LowerThanEquals extends Token {

    public LowerThanEquals(){super("LOWER_THAN_EQUALS", "<=");}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
