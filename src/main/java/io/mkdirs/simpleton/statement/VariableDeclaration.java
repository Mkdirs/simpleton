package io.mkdirs.simpleton.statement;

import io.mkdirs.simpleton.model.token.Token;

public class VariableDeclaration extends Statement{

    public VariableDeclaration name(String name){
        setInfo("name", name);
        return this;
    }

    public String name(){return getInfo("name");}

    public VariableDeclaration type(Token type){
        setInfo("type", type);
        return this;
    }

    public Token type(){return getInfo("type");}

    public VariableDeclaration value(String value){
        setInfo("value", value);
        return this;
    }

    public String value(){return getInfo("value");}
}
