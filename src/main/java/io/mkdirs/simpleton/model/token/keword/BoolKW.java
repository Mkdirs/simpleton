package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class BoolKW extends Token {

    public BoolKW(int line, int column){super(TokenKind.BOOL_KW, line, column);}
}
