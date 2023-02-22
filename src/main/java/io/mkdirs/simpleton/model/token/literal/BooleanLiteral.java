package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class BooleanLiteral extends LiteralValueToken {


    public BooleanLiteral(String value, int line, int column) {
        super(TokenKind.BOOL_LITERAL, value, line, column);
    }
}
