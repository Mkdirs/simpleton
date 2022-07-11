package io.mkdirs.simpleton.statement;

public class EndOfClosure extends Statement{

    public EndOfClosure line(int line){
        setInfo("line", line);
        return this;
    }

    public int line(){return getInfo("line");}
}
