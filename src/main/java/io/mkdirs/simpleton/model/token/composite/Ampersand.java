package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Ampersand extends Token implements IComposable{

    public Ampersand(){super(TokenKind.AMPERSAND);}


    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new And();

        return null;
    }
}
