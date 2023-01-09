package io.mkdirs.simpleton.model.token.keword;

import io.mkdirs.simpleton.model.token.Token;

public class FunctionKW extends Token {

    public FunctionKW(){super("FUNCTION_KW", "function");}

    @Override
    public boolean isKeyword() {
        return true;
    }
    
}
