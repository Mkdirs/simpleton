package io.mkdirs.simpleton.forest_builder.structure;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.TreeBuilder;
import io.mkdirs.simpleton.forest_builder.TreeBuilderResult;
import io.mkdirs.simpleton.model.error.StackableErrorBuilder;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.model.token.composite.VariableName;
import io.mkdirs.simpleton.model.token.keword.FunctionKW;
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
                Simpleton.match(tokens, "def_kw function_kw func colon type do_kw l_bracket eol")
                || Simpleton.match(tokens, "def_kw function_kw func colon void_kw do_kw l_bracket eol")
        );
    }

    private boolean validParams(List<Token> params){
        if(params.isEmpty())
            return true;

        TokenKind expectedTokenKind = TokenKind.VAR_NAME;
        boolean expectType = false;
        boolean loop = true;
        int i = 0;

        while(loop && i < params.size()){
            Token token = params.get(i);


            if(expectedTokenKind.equals(token.kind) && !expectType) {
                if (TokenKind.VAR_NAME.equals(token.kind)) {
                    expectedTokenKind = TokenKind.COLON;
                } else if (TokenKind.COLON.equals(token.kind)) {
                    expectType = true;
                } else if (TokenKind.COMMA.equals(token.kind)) {
                    expectedTokenKind = TokenKind.VAR_NAME;
                }
            }else if(token.kind.group.contains("type") && expectType) {
                expectType = false;
                expectedTokenKind = TokenKind.COMMA;
            }else{
                loop = false;
            }
            i++;
        }

        if(! params.get(i-1).kind.group.contains("type"))
            return false;

        return loop;
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {
        if(!isValid(tokens))
            return super.build(tokens);

        ASTNode root = new ASTNode(tokens.get(0));
        Func func = (Func) tokens.get(2);


        var params = func.getBody().subList(1, func.getBody().size()-1);//tokens.subList(4, tokens.indexOf(Token.R_PAREN));
        if(!validParams(params))
            return new TreeBuilderResult(Result.failure(
                    new StackableErrorBuilder("Cannot resolve function parameters")
                            .withStatement("")
                            .build()
            ), 0);

        ASTNode function = new ASTNode(tokens.get(1));

        //Add the name
        function.addChild(new ASTNode(func));

        //Add the parameters
        Token name = null;
        for(Token token : params){
            if(TokenKind.L_PAREN.equals(token.kind) || TokenKind.R_PAREN.equals(token.kind))
                continue;

            if(TokenKind.VAR_NAME.equals(token.kind)){
                name = token;
            }else if(token.kind.group.contains("type")){
                ASTNode param = new ASTNode(name);
                param.addChild(new ASTNode(token));

                function.addChild(param);
            }
        }

        //Add the return type
        int indexOfColon = tokens.indexOf(
                tokens.stream()
                        .filter(e -> TokenKind.COLON.equals(e.kind))
                        .findFirst().orElse(null)
        );
        Token returnType = tokens.get(indexOfColon+1);
        function.addChild(new ASTNode(returnType));


        //Add the body of the function
        int indexOfEOL = tokens.indexOf(
                tokens.stream()
                        .filter(e -> TokenKind.EOL.equals(e.kind))
                        .findFirst().orElse(null)
        );
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
