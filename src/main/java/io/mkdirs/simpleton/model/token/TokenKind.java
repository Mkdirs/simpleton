package io.mkdirs.simpleton.model.token;

public enum TokenKind {

    //Keywords
    BOOL_KW("bool", "keyword type"),
    CHAR_KW("char", "keyword type"),
    STRING_KW("string","keyword type"),
    INT_KW("int","keyword type"),
    FLOAT_KW("float","keyword type"),
    DEF_KW("def", "keyword"),
    DO_KW("do", "keyword"),
    ELSE_KW("else","keyword"),
    FUNCTION_KW("function","keyword"),
    IF_KW("if","keyword"),
    LET_KW("let","keyword"),
    NULL_KW("null","keyword"),
    RETURN_KW("return","keyword"),
    THEN_KW("then","keyword"),
    VOID_KW("void","keyword"),
    WHILE_KW("while","keyword"),



    //Composites
    AMPERSAND("&"), AND("&&"),
    EQUALS("="), EQUALITY("=="),
    FUNC,
    GREATER_THAN(">"), GREATER_THAN_EQ(">="),
    INEQUALITY("!="),
    LOWER_THAN("<"), LOWER_THAN_EQ("<="),
    NOT("!"),
    PIPE("|"), OR("||"),
    VAR_NAME,



    //Single characters
    COLON(":"),
    COMMA(","),
    DIVIDE("/"),
    EOL("\n"),
    L_BRACKET("{"), R_BRACKET("}"),
    L_PAREN("("), R_PAREN(")"),
    MINUS("-"),
    PLUS("+"),
    STAR("*"),
    PERCENT("%"),



    //Literals
    BOOL_LITERAL,
    CHAR_LITERAL,
    INT_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL;

    public final String literal;
    public final String group;
    TokenKind(String literal, String group){
        this.literal = literal;
        this.group = group;
    }

    TokenKind(String literal){this(literal, "");}
    TokenKind(){this("", "");}
    public boolean isKeyword(){
        return  hasLiteral() && group.contains("keyword");
    }
    public boolean hasLiteral(){return !literal.isEmpty();}
    public boolean isSingleChar(){return hasLiteral() && literal.length() == 1;}

}
