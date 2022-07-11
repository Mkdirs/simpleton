package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class GreaterThan extends Token implements IComposable{

    public GreaterThan(){super("GREATER_THAN", ">");}

    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public Token compose(Token token) {
        if(Token.EQUALS.equals(token))
            return new GreaterThanEquals();

        return null;
    }
}
