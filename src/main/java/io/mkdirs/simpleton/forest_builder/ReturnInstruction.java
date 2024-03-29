package io.mkdirs.simpleton.forest_builder;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public class ReturnInstruction extends TreeBuilder{

    private final ExpressionEvaluator evaluator;
    public ReturnInstruction(ExpressionEvaluator evaluator, TreeBuilder head){
        super(head);
        this.evaluator = evaluator;
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "return_kw * eol");
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {
        if(!isValid(tokens))
            return super.build(tokens);

        ASTNode root = new ASTNode(tokens.get(0));
        int indexOfEOL = tokens.indexOf(
                tokens.stream()
                        .filter(e -> TokenKind.EOL.equals(e.kind))
                        .findFirst().orElse(null)
        );

        if(!TokenKind.EOL.equals(tokens.get(1).kind)){
            var res = evaluator.buildTree(tokens.subList(1, indexOfEOL));

            if(res.isFailure())
                return new TreeBuilderResult(res, 0);


            root.addChild(res.get());
        }



        return new TreeBuilderResult(Result.success(root), indexOfEOL+1);


    }
}
