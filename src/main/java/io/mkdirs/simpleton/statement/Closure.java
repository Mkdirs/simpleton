package io.mkdirs.simpleton.statement;

public abstract class Closure extends Statement{

    public Closure value(String value){
        setInfo("value", value);
        return this;
    }

    public Closure line(int line){
        setInfo("line", line);
        return this;
    }

    public Closure end(EndOfClosure end){
        setInfo("end", end);
        return this;
    }

    public String value(){return getInfo("value");}
    public EndOfClosure end(){return getInfo("end");}
    public int line(){return getInfo("line");}
}
