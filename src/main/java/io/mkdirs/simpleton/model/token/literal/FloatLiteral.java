package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.Token;

public class FloatLiteral extends Token {

    public FloatLiteral(String literal) {
        super("FLOAT_LITERAL", literal);
    }

    public FloatLiteral(){this(null);}


    @Override
    public boolean isKeyword() {
        return false;
    }
}
