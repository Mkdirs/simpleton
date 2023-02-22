package io.mkdirs.simpleton.model.token;


public abstract class Token {


    public final TokenKind kind;
    public final int line,column;


    protected Token(TokenKind kind, int line, int column){
        this.kind = kind;
        this.line = line;
        this.column = column;
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

    public String toRaw(){
        if(kind.hasLiteral())
            return kind.literal;
        return "";
    }

    @Override
    public String toString() {
        return "Token." + kind + (!kind.literal.isEmpty() ? "('"+kind.literal+"')" : "");
    }
}
