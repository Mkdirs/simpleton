package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.model.token.literal.LiteralValueToken;
import io.mkdirs.simpleton.result.Result;

import java.util.ArrayList;
import java.util.List;

public class Func extends Token {

    private List<Token> body = new ArrayList<>();
    private final List<Token> rawArgs = new ArrayList<>();
    private final List<LiteralValueToken> args = new ArrayList<>();

    private boolean computedArgs = false;
    //private int fullBodyLength = 0;

    public final String name;

    public Func(String name){
        super(TokenKind.FUNC);
        this.name = name;
    }


    public List<Token> getBody() {
        return body;
    }

    public List<Token> getRawArgs(){return rawArgs;}
    public List<LiteralValueToken> getArgs(){return args;}

    public boolean areArgsComputed() {
        return computedArgs;
    }

    private Result<LiteralValueToken> computeArg(List<Token> tokens, ExpressionEvaluator evaluator){
        Result<ASTNode> res =  evaluator.buildTree(tokens);

        if(res.isFailure())
            return Result.failure(res.getMessage());

        Result<LiteralValueToken> resToken = evaluator.evaluate(res.get());

        if(resToken.isFailure())
            return resToken;

        Token tok = resToken.get();

        if(TokenKind.FUNC.equals(tok.kind)) {
            Result r = ((Func) tok).computeArgs(evaluator);

            if(r.isFailure())
                return r;
        }

        return Result.success((LiteralValueToken) tok);

    }

    public Result computeArgs(ExpressionEvaluator evaluator){
        List<Token> temp = new ArrayList<>();
        args.clear();

        for(Token t : body.subList(1, body.size()-1)){
            if(TokenKind.COMMA.equals(t.kind)){

                Result<LiteralValueToken> res = computeArg(temp, evaluator);
                if(res.isFailure())
                    return res;

                args.add(res.get());

                temp.clear();
                continue;
            }

            temp.add(t);
        }

        if(!temp.isEmpty()){
            Result<LiteralValueToken> res = computeArg(temp, evaluator);
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
        String argsStr = String.join(",", rawArgs.stream().map(e -> Type.typeOf(e.kind)).map(Type::name).toList());
        return name+"("+argsStr+")";
    }
}

