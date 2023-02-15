package io.mkdirs.simpleton.model.token.literal;

import io.mkdirs.simpleton.model.token.TokenKind;

public class VoidPlaceholder extends LiteralValueToken {

    public static final VoidPlaceholder VOID = new VoidPlaceholder();


    public VoidPlaceholder() {
        super(TokenKind.VOID_KW, null);
    }
}
