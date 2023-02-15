package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Not extends Token implements IComposable{

    public Not(){super(TokenKind.NOT);}


    @Override
    public Token compose(Token token) {
        if(TokenKind.EQUALS.equals(token.kind))
            return new Inequality();

        return null;
    }
}
