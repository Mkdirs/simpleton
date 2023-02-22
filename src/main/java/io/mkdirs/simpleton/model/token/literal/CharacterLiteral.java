package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class CharacterLiteral extends LiteralValueToken {

    public CharacterLiteral(String value, int line, int column) {
        super(TokenKind.CHAR_LITERAL, value, line, column);
    }

    @Override
    public String toRaw() {
        return "'"+super.toRaw()+"'";
    }
}
