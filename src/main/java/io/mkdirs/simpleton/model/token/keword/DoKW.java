package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class DoKW extends Token {

    public DoKW(int line, int column){super(TokenKind.DO_KW, line, column);}
}
