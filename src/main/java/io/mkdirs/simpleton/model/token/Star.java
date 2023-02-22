package io.mkdirs.simpleton.model.token;

public class Star extends Token {

    public Star(int line, int column) {
        super(TokenKind.STAR, line, column);
    }
}
