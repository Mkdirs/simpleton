package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.token.Token;

public class Operand {

    private static final int NOTHING_CODE = 0;
    private static final int SOME_CODE = 1;

    public static final Operand NOTHING = new Operand();

    public static final Operand with(Token token){
        return new Operand(token, SOME_CODE);
    }

    private final Token token;
    private final int code;
    private Operand(Token token, int code){
        this.token = token;
        this.code = code;
    }

    private Operand(){
        this(null, NOTHING_CODE);
    }

    public boolean match(Token t){
        if(this.equals(NOTHING))
            return (t == null);

        return this.token.equals(t);
    }

    public Token getToken() {
        return token;
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
