package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class IntKW extends Token {

    public IntKW(int line, int column){super(TokenKind.INT_KW, line, column);}
}
