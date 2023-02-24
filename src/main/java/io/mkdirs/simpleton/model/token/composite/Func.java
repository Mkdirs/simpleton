package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.Value;
import io.mkdirs.simpleton.model.error.StackableError;
import io.mkdirs.simpleton.model.error.StackableErrorBuilder;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;
import io.mkdirs.simpleton.result.Result;

import java.util.ArrayList;
import java.util.List;

public class Func extends Token {

    private List<Token> body = new ArrayList<>();
    private final List<Token> rawArgs = new ArrayList<>();
    private final List<Value> args = new ArrayList<>();

    private boolean computedArgs = false;
    //private int fullBodyLength = 0;

    public final String name;

    public Func(String name, int line, int column){
        super(TokenKind.FUNC, line, column);
        this.name = name;
    }


    public List<Token> getBody() {
        return body;
    }

    public List<Token> getRawArgs(){return rawArgs;}
    public List<Value> getArgs(){return args;}

    public boolean areArgsComputed() {
        return computedArgs;
    }

    private Result<Value, StackableError> computeArg(List<Token> tokens, ExpressionEvaluator evaluator){
        var res =  evaluator.buildTree(tokens);

        if(res.isFailure())
            return Result.failure(res.err());

        var resToken = evaluator.evaluate(res.get());

        if(resToken.isFailure())
            return resToken;

        Value val = resToken.get();

        if(TokenKind.FUNC.equals(res.get().getToken().kind)) {
            var r = ((Func) res.get().getToken()).computeArgs(evaluator);

            if(r.isFailure())
                return r;
        }

        return Result.success(val);

    }

    public Result computeArgs(ExpressionEvaluator evaluator){
        List<Token> temp = new ArrayList<>();
        args.clear();

        for(Token t : body.subList(1, body.size()-1)){
            if(TokenKind.COMMA.equals(t.kind)){

                var res = computeArg(temp, evaluator);
                if(res.isFailure())
                    return res;

                args.add(res.get());

                temp.clear();
                continue;
            }

            temp.add(t);
        }

        if(!temp.isEmpty()){
            var res = computeArg(temp, evaluator);
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
    public String toString() {
        String bodyStr = String.join(",", body.stream().map(Token::toString).toList());
        return "Token."+kind+"('"+name+"{"+bodyStr+"}')";
    }

    @Override
    public String toText() {
        String argsStr = String.join(",", args.stream().map(Value::type).map(Type::name).toList());
        return name+"("+argsStr+")";
    }
}

