package io.mkdirs.simpleton.model.token;

public class LBracket extends Token {

    public LBracket(int line, int column) {
        super(TokenKind.L_BRACKET, line, column);
    }
}
