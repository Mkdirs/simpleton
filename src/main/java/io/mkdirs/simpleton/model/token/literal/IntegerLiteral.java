package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.Token;

public class IntegerLiteral extends Token {

    public IntegerLiteral(String literal) {
        super("INTEGER_LITERAL", literal);
    }

    public IntegerLiteral(){this(null);}


    @Override
    public boolean isKeyword() {
        return false;
    }
}
