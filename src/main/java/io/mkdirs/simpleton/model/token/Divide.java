package io.mkdirs.simpleton.model.token;

public class Divide extends Token {

    public Divide() {
        super("DIVIDE", "/");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
