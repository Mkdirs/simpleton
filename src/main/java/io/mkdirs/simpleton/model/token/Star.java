package io.mkdirs.simpleton.model.token;

public class Star extends Token {

    public Star() {
        super("STAR", "*");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
