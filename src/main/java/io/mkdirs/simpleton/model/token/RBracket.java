package io.mkdirs.simpleton.model.token;

public class RBracket extends Token {

    public RBracket() {
        super("RIGHT_BRACKET", "}");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
