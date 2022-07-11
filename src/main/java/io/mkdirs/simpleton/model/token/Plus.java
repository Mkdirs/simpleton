package io.mkdirs.simpleton.model.token;

public class Plus extends Token {

    public Plus() {
        super("PLUS", "+");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
