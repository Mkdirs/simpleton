package io.mkdirs.simpleton.model;

import io.mkdirs.simpleton.model.token.Token;

import java.util.Arrays;

public enum Type {
    BOOLEAN(Token.BOOLEAN_LITERAL, Token.BOOL_KW),
    CHARACTER(Token.CHARACTER_LITERAL, Token.CHAR_KW),
    FLOAT(Token.FLOAT_LITERAL, Token.FLOAT_KW),
    INTEGER(Token.INTEGER_LITERAL, Token.INT_KW),
    STRING(Token.STRING_LITERAL, Token.STRING_KW),
    VOID(Token.VOID_KW),
    NULL(Token.NULL_KW);
    private final Token[] tokens;
    private Type(Token... tokens){
        this.tokens = tokens;
    }

    public static Type typeOf(Token token){
        var res = Arrays.stream(Type.values()).filter(e -> Arrays.stream(e.tokens).anyMatch(e2 -> e2.equals(token))).findFirst();

        if(res.isPresent())
            return res.get();

        return null;
    }
}
