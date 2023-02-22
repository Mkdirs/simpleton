package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Or extends Token {

    public Or(int line, int column){super(TokenKind.OR, line, column);}

}
