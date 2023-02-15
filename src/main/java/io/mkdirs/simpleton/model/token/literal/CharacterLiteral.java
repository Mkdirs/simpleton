package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class CharacterLiteral extends LiteralValueToken {

    public CharacterLiteral(String value) {
        super(TokenKind.CHAR_LITERAL, value);
    }
}
