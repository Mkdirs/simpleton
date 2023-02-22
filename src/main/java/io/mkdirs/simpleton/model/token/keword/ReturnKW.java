package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class ReturnKW extends Token {

    public ReturnKW(int line, int column){super(TokenKind.RETURN_KW, line, column);}

}
