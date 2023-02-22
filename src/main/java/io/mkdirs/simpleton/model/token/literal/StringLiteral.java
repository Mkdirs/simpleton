package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class StringLiteral extends LiteralValueToken {

    public StringLiteral(String value, int line, int column) {
        super(TokenKind.STRING_LITERAL, value, line, column);
    }

    @Override
    public String toRaw() {
        return "\""+super.toRaw()+"\"";
    }
}
