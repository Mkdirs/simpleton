package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class Pipe extends Token implements IComposable{

    public Pipe(){super("PIPE", "|");}

    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public Token compose(Token token) {
        if(this.equals(token))
            return new Or();

        return null;
    }
}
