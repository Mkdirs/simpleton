package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FuncSignature {
    private final String name;
    private final Map<String, Type> args;
    private final Type returnType;
    private final Location location;

    public FuncSignature(String name, Map<String, Type> args, Type returnType, Location location){
        this.name = name;
        this.args = args;
        this.returnType = returnType;
        this.location = location;
    }

    public FuncSignature(String name, Map<String, Type> args, Type returnType){this(name, args, returnType, Location.BUILTINS);}
    public FuncSignature(String name, Map<String, Type> args, Location location){this(name, args, Type.VOID, location);}

    public FuncSignature(String name, Map<String, Type> args){this(name, args, Type.VOID, Location.BUILTINS);}



    public String getName() {
        return name;
    }

    public Map<String, Type> getArgs() {
        return args;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Location getLocation() {
        return location;
    }

    public boolean match(Func other){

        if(! name.equals(other.name))
            return false;

        if(args.size() != other.getArgs().size())
            return false;

        Type[] signatureTypes = args.values().toArray(Type[]::new);
        Type[] candidateTypes = other.getArgs().stream().map(e -> Type.typeOf(e.kind)).toArray(Type[]::new);
        boolean valid = true;
        int i = 0;
        while(valid && i < signatureTypes.length){
            Type signature = signatureTypes[i];
            Type candidate = candidateTypes[i];
            if(!signature.equals(candidate) && !Type.NULL.equals(candidate)){
                valid = false;
                continue;
            }

            i++;

        }

        return valid;//Arrays.equals(args.values().toArray(Type[]::new), other.getArgs().stream().map(e -> Type.typeOf(e.kind)).toArray(Type[]::new));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args, returnType);
    }

    public boolean partialEquals(FuncSignature other){
        if(other == null)
            return false;

        Type[] signatureTypes = args.values().toArray(Type[]::new);
        Type[] candidateTypes = other.args.values().toArray(Type[]::new);


        return name.equals(other.name) && Arrays.equals(signatureTypes, candidateTypes);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(!FuncSignature.class.equals(obj.getClass()))
            return false;

        FuncSignature other = (FuncSignature) obj;

        return partialEquals(other) && returnType.equals(other.returnType);
    }

    @Override
    public String toString() {
        return name+"(" + args.values().stream().map(e -> e.name()).collect(Collectors.joining(", ")) + ")";
    }
}
