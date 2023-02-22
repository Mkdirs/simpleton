package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class AnyKW extends Token {

    public AnyKW(int line, int column){super(TokenKind.ANY_KW, line, column);}

}
