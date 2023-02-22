package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Ampersand extends Token implements IComposable{

    public Ampersand(int line, int column){super(TokenKind.AMPERSAND, line, column);}


    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new And(line, column);

        return null;
    }
}
