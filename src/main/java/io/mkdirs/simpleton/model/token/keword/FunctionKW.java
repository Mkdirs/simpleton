package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class FunctionKW extends Token {

    public FunctionKW(int line, int column){super(TokenKind.FUNCTION_KW, line, column);}
    
}
