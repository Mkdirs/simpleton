package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Equality extends Token {

    public Equality(int line, int column){super(TokenKind.EQUALITY, line, column);}
}
