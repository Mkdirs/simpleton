package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.ScopeContext;

import java.util.*;

public class Simpleton {


    public Result<List<Token>> build(String text){
        String[] lines = text.split("\n");
        int lineIndex = 0;

        Lexer lexer = new Lexer();
        List<Token> res = new ArrayList<>();

        while(lineIndex < lines.length){
            String line = lines[lineIndex];
            if(line.isBlank()){
                lineIndex++;
                continue;
            }

            Result<List<Token>> tokensResult = lexer.parse(line);
            if(tokensResult.isFailure()){
                return Result.failure(tokensResult.getMessage());
            }

            res.addAll(tokensResult.get());


            lineIndex++;
        }


        return Result.success(res);
    }

    public Result<List<ASTNode>> build(List<Token> tokens){

        return Result.failure("TODO");
    }

    private static boolean match(Collection<Token> tokens, String statement){
        return Arrays.toString(tokens.stream().map(Token::getName).toArray()).equals(statement.replace(" ", ", "));
    }

}
