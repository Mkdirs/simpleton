package io.mkdirs.simpleton.statement;

import io.mkdirs.simpleton.model.token.composite.Func;

public class FuncCall extends Statement{

    public FuncCall func(Func func){
        setInfo("func", func);
        return this;
    }

    public Func func(){return getInfo("func");}
}
