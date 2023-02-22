package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class DefKW extends Token {

    public DefKW(int line, int column){super(TokenKind.DEF_KW, line, column);}

}
