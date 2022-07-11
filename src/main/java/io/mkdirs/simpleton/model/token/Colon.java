package io.mkdirs.simpleton.model.token;

public class Colon extends Token {

    public Colon() {
        super("COLON", ":");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
