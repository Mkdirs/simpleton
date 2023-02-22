package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class And extends Token {

    public And(int line, int column){super(TokenKind.AND, line, column);}

}
