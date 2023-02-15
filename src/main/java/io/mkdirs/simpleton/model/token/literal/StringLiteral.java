package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class StringLiteral extends LiteralValueToken {

    public StringLiteral(String value) {
        super(TokenKind.STRING_LITERAL, value);
    }

}
