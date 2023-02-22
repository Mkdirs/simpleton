package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class LowerThan extends Token implements IComposable{

    public LowerThan(int line, int column){super(TokenKind.LOWER_THAN, line, column);}


    @Override
    public Token compose(Token token) {
        if(TokenKind.EQUALS.equals(token.kind))
            return new LowerThanEquals(line, column);

        return null;
    }
}
