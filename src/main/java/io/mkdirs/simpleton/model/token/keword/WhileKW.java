package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class WhileKW extends Token {

    public WhileKW(int line, int column){super(TokenKind.WHILE_KW, line, column);}
}
