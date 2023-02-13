package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.ScopeContext;

import java.util.ArrayList;
import java.util.List;

public class Func extends Token {

    private List<Token> body = new ArrayList<>();
    private final List<Token> args = new ArrayList<>();

    private boolean computedArgs = false;
    //private int fullBodyLength = 0;

    public Func(String name){
        super("FUNC", name);
    }

    public Func(){
        this(null);
    }


    public List<Token> getBody() {
        return body;
    }
    public List<Token> getArgs(){return args;}

    public boolean areArgsComputed() {
        return computedArgs;
    }

    private Result<Token> computeArg(List<Token> tokens, ExpressionEvaluator evaluator){
        Result<ASTNode> res =  evaluator.buildTree(tokens);

        if(res.isFailure())
            return Result.failure(res.getMessage());

        Result<Token> resToken = evaluator.evaluate(res.get());

        if(resToken.isFailure())
            return resToken;

        Token tok = resToken.get();

        if(Token.FUNC.equals(tok)) {
            Result r = ((Func) tok).computeArgs(evaluator);

            if(r.isFailure())
                return r;
        }

        return Result.success(tok);

    }

    public Result computeArgs(ExpressionEvaluator evaluator){
        List<Token> temp = new ArrayList<>();
        args.clear();

        for(Token t : body.subList(1, body.size()-1)){
            if(Token.COMMA.equals(t)){

                Result<Token> res = computeArg(temp, evaluator);
                if(res.isFailure())
                    return res;

                args.add(res.get());

                temp.clear();
                continue;
            }

            temp.add(t);
        }

        if(!temp.isEmpty()){
            Result<Token> res = computeArg(temp, evaluator);
            if(res.isFailure())
                return res;

            args.add(res.get());

            temp.clear();
        }

        computedArgs = true;
        return Result.success(null);
    }


    public void addInBody(Token t){
        body.add(t);
    }

    public int getFullBodyLength() {
        int s = 0;
        for(Token t : body){
            if(this.equals(t))
                s += ((Func) t).getFullBodyLength();
            else
                s++;
        }
        return s;
    }

    public void setBody(List<Token> body) {
        this.body = body;
    }

    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public String toString() {
        String bodyStr = String.join(",", body.stream().map(Token::toString).toList());
        return "Token."+name+"('"+literal+"{"+bodyStr+"}')";
    }

    @Override
    public String toText() {
        String argsStr = String.join(",", args.stream().map(Type::typeOf).map(Type::name).toList());
        return literal+"("+argsStr+")";
    }
}

