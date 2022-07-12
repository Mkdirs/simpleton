package io.mkdirs.simpleton.model.token;

public class Comma extends Token {

    public Comma() {
        super("COMMA", ",");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
