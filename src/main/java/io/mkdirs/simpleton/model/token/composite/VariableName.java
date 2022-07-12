package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public class VariableName extends Token implements IComposable{

    public VariableName(String literal){super("VARIABLE_NAME", literal);}
    public VariableName() {
        this(null);
    }


    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public Token compose(Token token) {
        if(Token.L_PAREN.equals(token))
            return new Func(this.literal);

        return null;
    }
}
