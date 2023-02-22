package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class IfKW extends Token {

    public IfKW(int line, int column){super(TokenKind.IF_KW, line, column);}
}
