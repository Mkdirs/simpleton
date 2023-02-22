package io.mkdirs.simpleton.evaluator.operator;

import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.Value;

public class Operand {

    private static final int NOTHING_CODE = 0;
    private static final int SOME_CODE = 1;

    public static final Operand NOTHING = new Operand();

    public static final Operand with(Type type){
        return new Operand(type, SOME_CODE);
    }

    private final Type type;
    private final int code;
    private Operand(Type type, int code){
        this.type = type;
        this.code = code;
    }

    private Operand(){
        this(null, NOTHING_CODE);
    }

    public boolean match(Value val){
        if(this.equals(NOTHING))
            return (val == null);
        else if(val == null)
            return false;

        return type.equals(val.type());
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(!obj.getClass().equals(this.getClass()))
            return false;

        Operand other = (Operand) obj;

        return this.code == other.code;
    }
}
