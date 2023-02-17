package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.func_executor.IFuncExecutor;
import io.mkdirs.simpleton.func_executor.NativeFuncExecutor;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;
import io.mkdirs.simpleton.result.Result;

import java.util.*;
import java.util.stream.Collectors;

public class ScopeContext {

    private final ScopeContext parent;
    private final int id;
    private final HashMap<String, VariableHolder> variables = new HashMap<>();
    private final List<FuncSignature> functions = new LinkedList<>();

    private final IFuncExecutor nativeFuncExecutor = new NativeFuncExecutor();

    private String line;

    public ScopeContext(ScopeContext parent, int id){

        this.parent = parent;
        this.id = id;

        pushFunctionSign(new FuncSignature("print", new LinkedHashMap(Map.of("object", Type.STRING))));
        pushFunctionSign(new FuncSignature("print", new LinkedHashMap(Map.of("object", Type.CHARACTER))));
        pushFunctionSign(new FuncSignature("print", new LinkedHashMap(Map.of("object", Type.INTEGER))));
        pushFunctionSign(new FuncSignature("print", new LinkedHashMap(Map.of("object", Type.FLOAT))));
        pushFunctionSign(new FuncSignature("print", new LinkedHashMap(Map.of("object", Type.BOOLEAN))));


        pushFunctionSign(new FuncSignature("input", new LinkedHashMap(Map.of("prompt", Type.STRING)), Type.STRING));
    }

    public ScopeContext(){this(null, 0);}

    public String getLine(){return this.line;}

    public void setLine(String line) {
        this.line = line;
    }

    public IFuncExecutor getNativeFuncExecutor() {
        return nativeFuncExecutor;
    }


    public ScopeContext child(){
        return new ScopeContext(this, this.id+1);
    }

    public ScopeContext getParent(){return this.parent;}

    public int getId() {
        return id;
    }

    public void pushFunctionSign(FuncSignature signature){
        this.functions.add(signature);
    }

    public boolean hasFunctionSign(FuncSignature signature){
        return this.functions.contains(signature);
    }

    public Result<FuncSignature> getFunctionSign(Func func, Type returnType) throws IllegalStateException{
        if(!func.areArgsComputed())
            throw new IllegalStateException("Function "+func+" has not computed its arguments !");


        List<FuncSignature> candidates;


        if(returnType != null){
            candidates = this.functions.stream()
                    .filter(e -> e.match(func) && e.getReturnType().equals(returnType))
                    .toList();
        }else{
            candidates = this.functions.stream()
                    .filter(e -> e.match(func))
                    .toList();
        }

        if(candidates.size() == 1)
            return Result.success(candidates.get(0));

        if(candidates.size() > 1)
            return Result.failure("Cannot determine the signature of '"+func.toText()+"'");

        if(this.parent != null)
            return this.parent.getFunctionSign(func, returnType);

        return Result.failure("Function '"+(func.toText())+"' does not exist");
    }

    public void pushVariable(String name, Type type, LiteralValueToken value){
        this.variables.put(name, new VariableHolder(type, value));
    }


    public void pushVariable(String name, Type type){
        this.variables.put(name, new VariableHolder(type));
    }


    public Optional<VariableHolder> getVariable(String name){
        if(this.variables.containsKey(name))
            return Optional.of(this.variables.get(name));

        if(this.parent != null)
            return this.parent.getVariable(name);

        return Optional.empty();
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(!obj.getClass().equals(this.getClass()))
            return false;

        ScopeContext other = (ScopeContext) obj;

        return this.id == other.id;
    }
}
