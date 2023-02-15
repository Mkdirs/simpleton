package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Pipe extends Token implements IComposable{

    public Pipe(){super(TokenKind.PIPE);}


    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new Or();

        return null;
    }
}
