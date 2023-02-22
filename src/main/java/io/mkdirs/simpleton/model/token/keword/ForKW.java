package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class ForKW extends Token {

    public ForKW(int line, int column){super(TokenKind.FOR_KW, line, column);}
}
