package io.mkdirs.simpleton.model;

public record Value(Type type, String value){
    public static final Value NULL = new Value(Type.NULL, null);
    public static final Value VOID = new Value(Type.VOID, null);
}
