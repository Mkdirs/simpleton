package io.mkdirs.simpleton.model;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

import java.util.Arrays;

public enum Type {
    BOOLEAN(TokenKind.BOOL_LITERAL, TokenKind.BOOL_KW),
    CHARACTER(TokenKind.CHAR_LITERAL, TokenKind.CHAR_KW),
    FLOAT(TokenKind.FLOAT_LITERAL, TokenKind.FLOAT_KW),
    INTEGER(TokenKind.INT_LITERAL, TokenKind.INT_KW),
    STRING(TokenKind.STRING_LITERAL, TokenKind.STRING_KW),
    VOID(TokenKind.VOID_KW),
    NULL(TokenKind.NULL_KW),
    UNKNOWN();
    private final TokenKind[] tokenKinds;
    private Type(TokenKind... tokenKinds){
        this.tokenKinds = tokenKinds;
    }

    public static Type typeOf(TokenKind tokenKind){
        var res = Arrays.stream(Type.values()).filter(e -> Arrays.stream(e.tokenKinds).anyMatch(e2 -> e2.equals(tokenKind))).findFirst();

        if(res.isPresent())
            return res.get();

        return null;
    }
}
