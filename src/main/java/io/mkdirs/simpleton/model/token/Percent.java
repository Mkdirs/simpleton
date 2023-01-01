package io.mkdirs.simpleton.model.token;

public class Percent extends Token {

    public Percent() {
        super("PERCENT", "%");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
