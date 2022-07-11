package io.mkdirs.simpleton.model.token;

public class LBracket extends Token {

    public LBracket() {
        super("LEFT_BRACKET", "{");
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
