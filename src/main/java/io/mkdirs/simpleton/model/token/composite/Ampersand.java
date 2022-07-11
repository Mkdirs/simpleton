package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class Ampersand extends Token implements IComposable{

    public Ampersand(){super("AMPERSAND", "&");}

    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new And();

        return null;
    }
}
