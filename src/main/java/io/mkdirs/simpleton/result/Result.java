package io.mkdirs.simpleton.result;

public class Result<T> {

    private final T value;
    private final boolean isFailure;
    private boolean isTerminative = false;
    private final String message;

    public static <U> Result<U> success(U value){
        return new Result<U>(value);
    }

    public static Result failure(String message){
        return new Result(message);
    }

    protected Result(T value){
        this.value = value;
        this.isFailure = false;
        this.message = null;
    }

    protected Result(String message){
        this.value = null;
        this.isFailure = true;
        this.message = message;
    }


    public T get(){return this.value;}

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

    public String getMessage() {
        return message;
    }
}
