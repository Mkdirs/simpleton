package io.mkdirs.simpleton.model.token;

public class Token {
    private final TokenType type;
    private final Object literal;
    public Token(TokenType type, Object literal){
        this.type = type;
        this.literal = literal;
    }

    public Token(TokenType type){
        this(type, null);
    }

    public boolean hasLiteral(){return this.literal != null;}

    public TokenType getType() {
        return type;
    }

    public Object getLiteral() {
        return literal;
    }
}
