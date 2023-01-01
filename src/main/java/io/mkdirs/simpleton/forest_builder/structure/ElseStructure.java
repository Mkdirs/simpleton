package io.mkdirs.simpleton.forest_builder.structure;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.TreeBuilder;
import io.mkdirs.simpleton.forest_builder.TreeBuilderResult;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public class ElseStructure extends AbstractStructure {

    private ExpressionEvaluator evaluator;

    public ElseStructure(ExpressionEvaluator evaluator, TreeBuilder head){
        super(head);
        this.evaluator = evaluator;

    }

    public ElseStructure(ExpressionEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "else_kw left_bracket eol");
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {
        if(!isValid(tokens))
            return super.build(tokens);

        ASTNode root = new ASTNode(Token.ELSE_KW);

        int indexOfEOL = tokens.indexOf(Token.EOL);
        TreeBuilderResult bodyResult = buildBody(tokens.subList(indexOfEOL+1, tokens.size()));

        if(bodyResult.tree().isFailure())
            return bodyResult;

        root.addChild(bodyResult.tree().get());

        return new TreeBuilderResult(Result.success(root), bodyResult.jumpIndex()+indexOfEOL+1);

    }
}
