package io.mkdirs.simpleton.model.token;

public class RParen extends Token {

    public RParen() {
        super("RIGHT_PARENTHESIS", ")");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
