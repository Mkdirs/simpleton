package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class StringKW extends Token {

    public StringKW(int line, int column){super(TokenKind.STRING_KW, line, column);}

}
