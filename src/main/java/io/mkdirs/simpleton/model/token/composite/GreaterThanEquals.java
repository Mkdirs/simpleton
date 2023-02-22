package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class GreaterThanEquals extends Token {

    public GreaterThanEquals(int line, int column){super(TokenKind.GREATER_THAN_EQ, line, column);}
}
