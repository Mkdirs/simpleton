package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class VoidKW extends Token {

    public VoidKW(int line, int column){super(TokenKind.VOID_KW, line, column);}

}
