package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.func_executor.IFuncExecutor;
import io.mkdirs.simpleton.func_executor.NativeFuncExecutor;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ScopeContext {

    private final ScopeContext parent;
    private final int id;
    private final HashMap<String, VariableHolder> variables = new HashMap<>();
    private final List<FuncSignature> functions = new ArrayList<>();

    private final IFuncExecutor nativeFuncExecutor = new NativeFuncExecutor();

    private String line;
    private boolean expectStructureClosure = false;
    private boolean skipIf = false;

    public ScopeContext(ScopeContext parent, int id){

        this.parent = parent;
        this.id = id;

        pushFunctionSign(new FuncSignature("print", new Token[]{Token.STRING_LITERAL}));
        pushFunctionSign(new FuncSignature("print", new Token[]{Token.CHARACTER_LITERAL}));
        pushFunctionSign(new FuncSignature("print", new Token[]{Token.INTEGER_LITERAL}));
        pushFunctionSign(new FuncSignature("print", new Token[]{Token.FLOAT_LITERAL}));
        pushFunctionSign(new FuncSignature("print", new Token[]{Token.BOOLEAN_LITERAL}));


        pushFunctionSign(new FuncSignature("input", new Token[]{Token.STRING_LITERAL}, Token.STRING_LITERAL));
    }

    public ScopeContext(){this(null, 0);}

    public String getLine(){return this.line;}

    public void setLine(String line) {
        this.line = line;
    }

    public IFuncExecutor getNativeFuncExecutor() {
        return nativeFuncExecutor;
    }

    public void setExpectStructureClosure(boolean expectStructureClosure) {
        this.expectStructureClosure = expectStructureClosure;
    }

    public boolean isExpectingStructureClosure() {
        return expectStructureClosure;
    }

    public void setSkipIf(boolean skipIf) {
        this.skipIf = skipIf;
    }

    public boolean isSkippingIf() {
        return skipIf;
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

    public Optional<FuncSignature> getFunctionSign(Func func) throws IllegalStateException{
        if(!func.areArgsComputed())
            throw new IllegalStateException("Function "+func+" has not computed its arguments !");

        Optional<FuncSignature> opt = this.functions.stream()
                .filter(e -> e.match(func))
                .findFirst();

        if(opt.isPresent())
            return opt;

        if(this.parent != null)
            return this.parent.getFunctionSign(func);

        return Optional.empty();
    }

    public void pushVariable(String name, Token type, Token value){
        this.variables.put(name, new VariableHolder(type, value));
    }


    public void pushVariable(String name, Token type){
        this.variables.put(name, new VariableHolder(type));
    }


    public Optional<VariableHolder> getVariable(String name){
        if(this.variables.containsKey(name))
            return Optional.of(this.variables.get(name));

        if(this.parent != null)
            return this.parent.getVariable(name);

        return Optional.empty();
    }

    public Token typeOf(Token value){
        if(Token.INTEGER_LITERAL.equals(value))
            return Token.INT_KW;
        else if(Token.FLOAT_LITERAL.equals(value))
            return Token.FLOAT_KW;
        else if(Token.STRING_LITERAL.equals(value))
            return Token.STRING_KW;
        else if(Token.CHARACTER_LITERAL.equals(value))
            return Token.CHAR_KW;
        else if(Token.BOOLEAN_LITERAL.equals(value))
            return Token.BOOL_KW;
        else if(Token.NULL_KW.equals(value))
            return Token.NULL_KW;

        return null;
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
