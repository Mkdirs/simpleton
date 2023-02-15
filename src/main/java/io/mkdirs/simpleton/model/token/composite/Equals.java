package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Equals extends Token implements IComposable{

    public Equals(){super(TokenKind.EQUALS);}


    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new Equality();

        return null;
    }
}
