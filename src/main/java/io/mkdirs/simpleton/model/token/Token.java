package io.mkdirs.simpleton.model.token;

import java.util.ArrayList;
import java.util.List;

public final class Token {

    private static int current_id = 0;
    private static final List<Token> tokens = new ArrayList<>();

    public static final Token
            INTEGER_LITERAL = new Token("INTEGER_LITERAL"),
            FLOAT_LITERAL = new Token("FLOAT_LITERAL"),
            STRING_LITERAL = new Token("STRING_LITERAL"),
            CHARACTER_LITERAL = new Token("CHARACTER_LITERAL"),
            BOOLEAN_LITERAL = new Token("BOOLEAN_LITERAL"),

            VARIABLE_NAME = new Token("VARIABLE"),

            PLUS = new Token("PLUS","+"),
            MINUS = new Token("MINUS","-"),
            TIMES = new Token("TIMES","*"),
            DIVIDE = new Token("DIVIDE","/"),




            //Composite tokens
            EQUALITY = new Token("EQUALITY","=="),
            EQUALS = new Token("EQUALS","=")
                    .withSetupCollapse(EQUALITY),


            INEQUALITY = new Token("INEQUALITY","!="),
            NOT = new Token("NOT","!")
                    .withSetupCollapse(EQUALS, INEQUALITY),


            GREATER_THAN_EQUALS = new Token("GREATER_THAN_EQUALS", ">="),
            GREATER_THAN = new Token("GREATER_THAN",">")
                    .withSetupCollapse(EQUALS, GREATER_THAN_EQUALS),

            SMALLER_THAN_EQUALS = new Token("SMALLER_THAN_EQUALS", "<="),
            SMALLER_THAN = new Token("SMALLER_THAN","<")
                    .withSetupCollapse(EQUALS, SMALLER_THAN_EQUALS),






            AND = new Token("AND","&&"),
            OR = new Token("OR","||"),

            LEFT_PARENTHESIS = new Token("LEFT_PARENTHESIS","("),
            RIGHT_PARENTHESIS = new Token("RIGHT_PARENTHESIS",")"),

            //KW = Key Word
            INT_KW = new Token("INT_KW","int"),
            FLOAT_KW = new Token("FLOAT_KW","float"),
            STRING_KW = new Token("STRING_KW","string"),
            CHAR_KW = new Token("CHAR_KW","char"),
            BOOL_KW = new Token("BOOL_KW","bool"),

            END_OF_LINE = new Token("END_OF_LINE");

    private final int id;
    private final String literal;
    private final String name;
    private Token collapseObject;
    private Token collapsedForm;

    private Token(int id, String name, String literal){
        this.id = id;
        this.name = name;
        this.literal = literal;
        Token.tokens.add(this);
    }

    private Token(String name, String literal){
        this(Token.current_id, name, literal);

        Token.current_id++;
    }

    private Token(String name){
        this(name, null);
    }

    private Token withSetupCollapse(Token object, Token form){
        this.collapseObject = object;
        this.collapsedForm = form;

        return this;
    }

    private Token withSetupCollapse(Token form){
        this.collapseObject = this;
        this.collapsedForm = form;

        return this;
    }


    public static List<Token> values(){return Token.tokens;}

    public Token with(String literal){
        return new Token(this.id, this.name, literal);
    }

    public String getLiteral(){return this.literal;}

    public boolean canCollapseFrom(Token other){
        if(other == null)
            return false;

        return other.equals(this.collapseObject);
    }

    public Token getCollapsedForm(){return this.collapsedForm;}


    public boolean hasLiteral(){return this.literal != null;}

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(! obj.getClass().equals(this.getClass()))
            return false;

        Token token = (Token) obj;
        return this.id == token.id;
    }

    @Override
    public String toString() {
        String extra = this.hasLiteral() ? "('"+this.literal+"')" : "";
        return "Token."+this.name+extra;
    }
}
