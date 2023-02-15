package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class NullPlaceholder extends LiteralValueToken {

    public static final NullPlaceholder NULL = new NullPlaceholder();


    public NullPlaceholder() {
        super(TokenKind.NULL_KW, null);
    }
}
