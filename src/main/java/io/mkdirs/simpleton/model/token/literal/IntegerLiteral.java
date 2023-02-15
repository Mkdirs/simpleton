package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class IntegerLiteral extends LiteralValueToken {

    public IntegerLiteral(String value) {
        super(TokenKind.INT_LITERAL, value);
    }
}
