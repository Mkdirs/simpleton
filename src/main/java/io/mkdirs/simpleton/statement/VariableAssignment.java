package io.mkdirs.simpleton.statement;

public class VariableAssignment extends Statement{

    public VariableAssignment name(String name){
        setInfo("name", name);
        return this;
    }

    public String name(){return getInfo("name");}

    public VariableAssignment value(String value){
        setInfo("value", value);
        return this;
    }

    public String value(){return getInfo("value");}

    @Override
    public String toText() {
        return name()+" = '"+value()+"'";
    }
}
