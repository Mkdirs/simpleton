package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Not extends Token implements IComposable{

    public Not(int line, int column){super(TokenKind.NOT, line, column);}


    @Override
    public Token compose(Token token) {
        if(TokenKind.EQUALS.equals(token.kind))
            return new Inequality(line, column);

        return null;
    }
}
