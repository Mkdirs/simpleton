package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class IntegerLiteral extends LiteralValueToken {

    public IntegerLiteral(String value, int line, int column) {
        super(TokenKind.INT_LITERAL, value, line, column);
    }
}
