package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class LowerThanEquals extends Token {

    public LowerThanEquals(int line, int column){super(TokenKind.LOWER_THAN_EQ, line, column);}
}
