package io.mkdirs.simpleton.result;

public class Result<T> {

    private final T value;
    private final boolean isFailure;
    private final String message;

    public static <U> Result<U> success(U value){
        return new Result<U>(value);
    }

    public static Result failure(String message){
        return new Result(message);
    }

    private Result(T value){
        this.value = value;
        this.isFailure = false;
        this.message = null;
    }

    private Result(String message){
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

    public String getMessage() {
        return message;
    }
}
