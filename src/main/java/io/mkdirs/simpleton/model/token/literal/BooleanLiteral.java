package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.Token;

public class BooleanLiteral extends Token {


    public BooleanLiteral(String literal) {
        super("BOOLEAN_LITERAL", literal);
    }

    public BooleanLiteral(){this(null);}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
