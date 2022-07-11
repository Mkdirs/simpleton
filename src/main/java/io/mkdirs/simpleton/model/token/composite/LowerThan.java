package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class LowerThan extends Token implements IComposable{

    public LowerThan(){super("LOWER_THAN", "<");}

    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public Token compose(Token token) {
        if(Token.EQUALS.equals(token))
            return new LowerThanEquals();

        return null;
    }
}
