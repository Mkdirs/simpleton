package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class FloatKW extends Token {

    public FloatKW(int line, int column){super(TokenKind.FLOAT_KW, line, column);}
}
