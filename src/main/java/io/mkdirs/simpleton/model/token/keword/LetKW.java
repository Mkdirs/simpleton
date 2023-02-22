package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class LetKW extends Token {

    public LetKW(int line, int column){super(TokenKind.LET_KW, line, column);}
}
