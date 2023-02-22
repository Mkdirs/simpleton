package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.func_executor.IFuncExecutor;
import io.mkdirs.simpleton.func_executor.NativeFuncExecutor;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.Value;
import io.mkdirs.simpleton.model.error.StackableError;
import io.mkdirs.simpleton.model.error.StackableErrorBuilder;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.result.Result;

import java.util.*;

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

        pushFunctionSign(new FuncSignature("print", new LinkedHashMap(Map.of("object", Type.ANY))));
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
        return this.functions.stream()
                .anyMatch(e -> e.partialEquals(signature));
    }

    public Result<FuncSignature, StackableError> getFunctionSign(Func func) throws IllegalStateException{
        if(!func.areArgsComputed())
            throw new IllegalStateException("Function "+func+" has not computed its arguments !");


        var candidates = this.functions.stream()
                .filter(e -> e.match(func))
                .toList();

        if(candidates.size() == 1)
            return Result.success(candidates.get(0));

        if(candidates.size() > 1)
            return Result.failure(new StackableErrorBuilder("Cannot determine signature of "+func.toText())
                    .withStatement("")
                    .build()
            );


        if(this.parent != null)
            return this.parent.getFunctionSign(func);


        return Result.failure(new StackableErrorBuilder("Function '"+(func.toText())+"' does not exist")
                .withStatement("")
                .build()
        );
    }

    public void pushVariable(String name, Type type, Value value){
        this.variables.put(name, new VariableHolder(type, value));
    }


    public void pushVariable(String name, Type type){
        this.variables.put(name, new VariableHolder(type));
    }

    public void flushVariables(){
        this.variables.clear();
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
