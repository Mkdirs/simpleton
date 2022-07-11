package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class Equals extends Token implements IComposable{

    public Equals(){super("EQUALS", "=");}

    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new Equality();

        return null;
    }
}
