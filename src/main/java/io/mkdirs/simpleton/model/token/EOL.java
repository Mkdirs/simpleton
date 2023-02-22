package io.mkdirs.simpleton.model.token;

public class EOL extends Token{

    public EOL(int line, int column){
        super(TokenKind.EOL, line, column);
    }
}
