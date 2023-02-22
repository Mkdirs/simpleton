package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class NullKW extends Token {

    public NullKW(int line, int column){super(TokenKind.NULL_KW, line, column);}
}
