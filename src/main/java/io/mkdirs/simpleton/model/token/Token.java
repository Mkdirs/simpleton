package io.mkdirs.simpleton.model.token;


public abstract class Token {


    public final TokenKind kind;


    protected Token(TokenKind kind){
        this.kind = kind;
    }


    public final boolean isKeyword(){
        return kind.isKeyword();
    }



    @Override
    public int hashCode() {
        return kind.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(! obj.getClass().equals(this.getClass()))
            return false;

        Token token = (Token) obj;
        return kind.equals(token.kind);
    }

    public String toText(){
        return this.toString();
    }

    @Override
    public String toString() {
        return "Token." + kind + (!kind.literal.isEmpty() ? "('"+kind.literal+"')" : "");
    }
}
