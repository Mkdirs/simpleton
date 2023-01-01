package io.mkdirs.simpleton.forest_builder;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public class FullVariableInitialization extends TreeBuilder{

    private ExpressionEvaluator evaluator;

    public FullVariableInitialization(ExpressionEvaluator evaluator, TreeBuilder head){
        super(head);
        this.evaluator = evaluator;
    }

    public FullVariableInitialization(ExpressionEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "let_kw variable_name colon type equals * eol");
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {
        if(! isValid(tokens))
            return super.build(tokens);

        ASTNode root = new ASTNode(Token.EQUALS);

        ASTNode left = new ASTNode(Token.LET_KW);
        left.addChildren(
                new ASTNode(tokens.get(1)),
                new ASTNode(tokens.get(3))
        );

        int indexOfEOL = tokens.indexOf(Token.EOL);
        Result<ASTNode> exprRes = evaluator.buildTree(tokens.subList(5, indexOfEOL));


        if(exprRes.isFailure())
            return new TreeBuilderResult(exprRes, 0);

        root.addChildren(left, exprRes.get());

        return new TreeBuilderResult(Result.success(root), indexOfEOL+1);
    }
}
