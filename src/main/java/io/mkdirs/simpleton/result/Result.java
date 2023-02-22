package io.mkdirs.simpleton.result;

public class Result<U, V> {

    private final U value;
    private final V err;

    private final boolean isFailure;
    private boolean isTerminative = false;

    public static <U, V> Result<U, V> success(U value){
        return new Result<U, V>(value, null, false);
    }

    public static  <U, V> Result<U, V> failure(V err){
        return new Result<U, V>(null, err, true);
    }


    private Result(U value, V err, boolean isFailure){
        this.value = value;
        this.err = err;
        this.isFailure = isFailure;
    }


    public U get(){return this.value;}
    public V err(){return this.err;}

    public boolean isFailure() {
        return isFailure;
    }

    public boolean isSuccess() {
        return !isFailure;
    }

    public boolean isTerminative() {
        return isTerminative;
    }

    public void setTerminative(){this.isTerminative = true;}

}
