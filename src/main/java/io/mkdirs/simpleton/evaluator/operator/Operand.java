package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;

public class Operand {

    private static final int NOTHING_CODE = 0;
    private static final int SOME_CODE = 1;

    public static final Operand NOTHING = new Operand();

    public static final Operand with(TokenKind tokenKind){
        return new Operand(tokenKind, SOME_CODE);
    }

    private final TokenKind tokenKind;
    private final int code;
    private Operand(TokenKind tokenKind, int code){
        this.tokenKind = tokenKind;
        this.code = code;
    }

    private Operand(){
        this(null, NOTHING_CODE);
    }

    public boolean match(TokenKind t){
        if(this.equals(NOTHING))
            return (t == null);

        return this.tokenKind.equals(t);
    }

    public TokenKind getTokenKind() {
        return tokenKind;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(!obj.getClass().equals(this.getClass()))
            return false;

        Operand other = (Operand) obj;

        return this.code == other.code;
    }
}
