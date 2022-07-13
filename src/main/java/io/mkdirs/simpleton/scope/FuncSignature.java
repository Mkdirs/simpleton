package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;

import java.util.Arrays;

public class FuncSignature {
    private final String name;
    private final Token[] args;
    private final Token returnType;
    private final int location;

    public FuncSignature(String name, Token[] args, Token returnType, int location){
        this.name = name;
        this.args = args;
        this.returnType = returnType;
        this.location = location;
    }

    public FuncSignature(String name, Token[] args, Token returnType){this(name, args, returnType, -1);}
    public FuncSignature(String name, Token[] args, int location){this(name, args, Token.VOID_KW, location);}

    public FuncSignature(String name, Token[] args){this(name, args, Token.VOID_KW, -1);}



    public String getName() {
        return name;
    }

    public Token getReturnType() {
        return returnType;
    }

    public int getLocation() {
        return location;
    }

    public boolean match(Func other){
        if(! name.equals(other.getLiteral()))
            return false;

        if(args.length != other.getArgs().size())
            return false;


        return Arrays.equals(args, other.getArgs().toArray(Token[]::new));
    }
}
