package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class FloatLiteral extends LiteralValueToken {

    public FloatLiteral(String value, int line, int column) {
        super(TokenKind.FLOAT_LITERAL, value, line, column);
    }

}
