package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class VariableName extends Token implements IComposable{

    public final String name;

    public VariableName(String name, int line, int column){
        super(TokenKind.VAR_NAME, line, column);
        this.name = name;
    }


    @Override
    public Token compose(Token token) {
        if(TokenKind.L_PAREN.equals(token.kind))
            return new Func(name, line, column);

        return null;
    }
}
