package io.mkdirs.simpleton.model.token;

public class LParen extends Token {

    public LParen() {
        super("LEFT_PARENTHESIS", "(");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
