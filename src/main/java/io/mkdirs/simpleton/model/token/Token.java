package io.mkdirs.simpleton.model.token;

import io.mkdirs.simpleton.model.token.composite.*;
import io.mkdirs.simpleton.model.token.keword.*;
import io.mkdirs.simpleton.model.token.literal.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Token {

    public static final List<Token> values = new ArrayList<>();

    public static final Token
        INTEGER_LITERAL = add(new IntegerLiteral()),
        FLOAT_LITERAL = add(new FloatLiteral()),
        STRING_LITERAL = add(new StringLiteral()),
        CHARACTER_LITERAL = add(new CharacterLiteral()),
        BOOLEAN_LITERAL = add(new BooleanLiteral()),


        PLUS = add(new Plus()),
        MINUS = add(new Minus()),
        STAR = add(new Star()),
        DIVIDE = add(new Divide()),

        L_PAREN = add(new LParen()),
        R_PAREN = add(new RParen()),
        L_BRACKET = new LBracket(),
        R_BRACKET = new RBracket(),
        COLON = add(new Colon()),
        COMMA = add(new Comma()),



        //Composites----------------------------------------------------------
        EQUALITY = new Equality(),
        EQUALS = add(new Equals()),

        INEQUALITY = new Inequality(),
        NOT = add(new Not()),

        GREATER_THAN_EQUALS = new GreaterThanEquals(),
        GREATER_THAN = add(new GreaterThan()),

        LOWER_THAN_EQUALS = new LowerThanEquals(),
        LOWER_THAN = add(new LowerThan()),

        AND = new And(),
        AMPERSAND = add(new Ampersand()),

        OR = new Or(),
        PIPE = add(new Pipe()),


        VARIABLE_NAME = new VariableName(),
        FUNC = new Func(),

        //----------------------------------------------------------






        LET_KW = add(new LetKW()),
        INT_KW = add(new IntKW()),
        FLOAT_KW = add(new FloatKW()),
        STRING_KW = add(new StringKW()),
        CHAR_KW = add(new CharKW()),
        BOOL_KW = add(new BoolKW()),
        NULL_KW = add(new NullKW()),
        VOID_KW = new VoidKW(),
        IF_KW = new IfKW(),
        THEN_KW = new ThenKW()


                ;


    protected String literal;
    protected final String name;


    protected Token(String name, String literal){
        this.name = name;
        this.literal = literal;

    }

    protected Token(String name){
        this(name, null);
    }

    public String getLiteral(){return this.literal;}

    protected void setLiteral(String literal){this.literal = literal;}

    public boolean hasLiteral(){return this.literal != null;}

    public String getName(){return this.name;}

    public abstract boolean isKeyword();

    private static Token add(Token token){
        values.add(token);
        return token;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(! obj.getClass().equals(this.getClass()))
            return false;

        Token token = (Token) obj;
        return this.name.equals(token.name);
    }

    public String text(){
        return this.toString();
    }

    @Override
    public String toString() {
        return "Token." + this.name + (this.hasLiteral() ? "('"+this.literal+"')" : "");
    }
}
