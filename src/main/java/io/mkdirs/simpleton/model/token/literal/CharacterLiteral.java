package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.Token;

public class CharacterLiteral extends Token {

    public CharacterLiteral(String literal) {
        super("CHARACTER_LITERAL", literal);
    }

    public CharacterLiteral(){this(null);}

    @Override
    public boolean isKeyword() {
        return false;
    }
}
