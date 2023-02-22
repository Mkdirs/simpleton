package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Inequality extends Token {

    public Inequality(int line, int column){super(TokenKind.INEQUALITY, line, column);}
}
