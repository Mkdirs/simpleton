package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class Not extends Token implements IComposable{

    public Not(){super("NOT", "!");}

    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public Token compose(Token token) {
        if(Token.EQUALS.equals(token))
            return new Inequality();

        return null;
    }
}
