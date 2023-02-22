package io.mkdirs.simpleton.forest_builder;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public class StandaloneExpression extends TreeBuilder{

    private ExpressionEvaluator evaluator;

    public StandaloneExpression(ExpressionEvaluator evaluator, TreeBuilder head){
        super(head);
        this.evaluator = evaluator;
    }

    public StandaloneExpression(ExpressionEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "* eol");
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {

        if(!isValid(tokens))
            return super.build(tokens);

        int indexOfEOL = tokens.indexOf(
                tokens.stream()
                        .filter(e -> TokenKind.EOL.equals(e.kind))
                        .findFirst().orElse(null)
        );

        var res = evaluator.buildTree(tokens.subList(0, indexOfEOL));

        if(res.isFailure())
            return new TreeBuilderResult(res, 0);
        return new TreeBuilderResult(res, indexOfEOL+1);
    }
}
