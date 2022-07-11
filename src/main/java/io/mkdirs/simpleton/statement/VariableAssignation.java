package io.mkdirs.simpleton.statement;

import io.mkdirs.simpleton.model.token.Token;

public class VariableAssignation extends Statement{

    public VariableAssignation name(String name){
        setInfo("name", name);
        return this;
    }

    public String name(){return getInfo("name");}

    public VariableAssignation value(String value){
        setInfo("value", value);
        return this;
    }

    public String value(){return getInfo("value");}
}
