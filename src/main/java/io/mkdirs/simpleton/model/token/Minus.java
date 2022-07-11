package io.mkdirs.simpleton.model.token;

public class Minus extends Token {

    public Minus() {
        super("MINUS", "-");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
