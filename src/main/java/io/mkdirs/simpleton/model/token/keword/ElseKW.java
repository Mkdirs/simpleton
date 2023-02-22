package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class ElseKW extends Token {

    public ElseKW(int line, int column){super(TokenKind.ELSE_KW, line, column);}
}
