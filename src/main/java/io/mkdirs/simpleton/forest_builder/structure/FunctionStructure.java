package io.mkdirs.simpleton.forest_builder.structure;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.TreeBuilder;
import io.mkdirs.simpleton.forest_builder.TreeBuilderResult;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.model.token.composite.VariableName;
import io.mkdirs.simpleton.result.Result;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionStructure extends AbstractStructure {


    public FunctionStructure(TreeBuilder head){
        super(head);

    }

    public FunctionStructure() {
        super();
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return (
                Simpleton.match(tokens, "def_kw function_kw func colon type do_kw left_bracket eol")
                || Simpleton.match(tokens, "def_kw function_kw func colon void_kw do_kw left_bracket eol")
        );
    }

    private boolean validParams(List<Token> params){
        if(params.isEmpty())
            return true;

        Token expectedToken = Token.VARIABLE_NAME;
        boolean expectType = false;
        boolean loop = true;
        int i = 0;

        while(loop && i < params.size()){
            Token token = params.get(i);


            if(expectedToken.equals(token) && !expectType) {
                if (Token.VARIABLE_NAME.equals(token)) {
                    expectedToken = Token.COLON;
                } else if (Token.COLON.equals(token)) {
                    expectType = true;
                } else if (Token.COMMA.equals(token)) {
                    expectedToken = Token.VARIABLE_NAME;
                }
            }else if("TYPE".equals(token.group()) && expectType) {
                expectType = false;
                expectedToken = Token.COMMA;
            }else{
                loop = false;
            }
            i++;
        }

        if(! "TYPE".equals(params.get(i-1).group()))
            return false;

        return loop;
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {
        if(!isValid(tokens))
            return super.build(tokens);

        ASTNode root = new ASTNode(Token.DEF_KW);
        Func func = (Func) tokens.get(2);


        var params = func.getBody().subList(1, func.getBody().size()-1);//tokens.subList(4, tokens.indexOf(Token.R_PAREN));
        if(!validParams(params))
            return new TreeBuilderResult(Result.failure("Cannot resolve function parameters"), 0);

        ASTNode function = new ASTNode(Token.FUNCTION_KW);

        //Add the name
        function.addChild(new ASTNode(new VariableName(func.getLiteral())));

        //Add the parameters
        Token name = null;
        for(Token token : params){
            if(Token.L_PAREN.equals(token) || Token.R_PAREN.equals(token))
                continue;

            if(Token.VARIABLE_NAME.equals(token)){
                name = token;
            }else if("TYPE".equals(token.group())){
                ASTNode param = new ASTNode(name);
                param.addChild(new ASTNode(token));

                function.addChild(param);
            }
        }

        //Add the return type
        int indexOfColon = tokens.indexOf(Token.COLON);
        Token returnType = tokens.get(indexOfColon+1);
        function.addChild(new ASTNode(returnType));


        //Add the body of the function
        int indexOfEOL = tokens.indexOf(Token.EOL);
        int jmp = indexOfEOL+1;
        TreeBuilderResult bodyResult = buildBody(tokens.subList(jmp, tokens.size()));


        if(bodyResult.tree().isFailure())
            return bodyResult;

        ASTNode body = bodyResult.tree().get();


        jmp += bodyResult.jumpIndex();
        function.addChild(body);

        root.addChild(function);


        return new TreeBuilderResult(Result.success(root), jmp);

    }
}
