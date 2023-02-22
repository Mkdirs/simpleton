package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public abstract class LiteralValueToken extends Token {

    public final String value;

    protected LiteralValueToken(TokenKind kind, String value, int line, int column){
        super(kind, line, column);
        this.value = value;
    }

    @Override
    public String toRaw() {
        return value;
    }
}
