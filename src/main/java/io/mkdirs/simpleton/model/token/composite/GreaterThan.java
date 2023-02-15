package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class GreaterThan extends Token implements IComposable{

    public GreaterThan(){super(TokenKind.GREATER_THAN);}


    @Override
    public Token compose(Token token) {
        if(TokenKind.EQUALS.equals(token.kind))
            return new GreaterThanEquals();

        return null;
    }
}
