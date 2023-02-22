package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Pipe extends Token implements IComposable{

    public Pipe(int line, int column){super(TokenKind.PIPE, line, column);}


    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new Or(line, column);

        return null;
    }
}
