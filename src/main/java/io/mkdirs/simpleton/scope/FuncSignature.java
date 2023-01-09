package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FuncSignature {
    private final String name;
    private final Map<String, Token> args;
    private final Token returnType;
    private final int location;

    public FuncSignature(String name, Map<String, Token> args, Token returnType, int location){
        this.name = name;
        this.args = args;
        this.returnType = returnType;
        this.location = location;
    }

    public FuncSignature(String name, Map<String, Token> args, Token returnType){this(name, args, returnType, -1);}
    public FuncSignature(String name, Map<String, Token> args, int location){this(name, args, Token.VOID_KW, location);}

    public FuncSignature(String name, Map<String, Token> args){this(name, args, Token.VOID_KW, -1);}



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

        if(args.size() != other.getArgs().size())
            return false;


        return Arrays.equals(args.values().toArray(new Token[0]), other.getArgs().toArray(Token[]::new));
    }
}
