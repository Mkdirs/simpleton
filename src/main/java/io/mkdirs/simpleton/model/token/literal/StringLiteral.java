package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.Token;

public class StringLiteral extends Token {

    public StringLiteral(String literal) {
        super("STRING_LITERAL", literal);
    }

    public StringLiteral(){this(null);}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
