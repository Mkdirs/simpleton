package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Equals extends Token implements IComposable{

    public Equals(int line, int column){super(TokenKind.EQUALS, line, column);}


    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new Equality(line, column);

        return null;
    }
}
