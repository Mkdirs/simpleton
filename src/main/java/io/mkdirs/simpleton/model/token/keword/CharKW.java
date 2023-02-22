package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class CharKW extends Token {

    public CharKW(int line, int column){super(TokenKind.CHAR_KW, line, column);}
}
