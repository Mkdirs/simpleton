package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class LowerThan extends Token implements IComposable{

    public LowerThan(){super(TokenKind.LOWER_THAN);}


    @Override
    public Token compose(Token token) {
        if(TokenKind.EQUALS.equals(token.kind))
            return new LowerThanEquals();

        return null;
    }
}
