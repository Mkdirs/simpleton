package io.mkdirs.simpleton.forest_builder;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public class VariableAssignment extends TreeBuilder{

    private ExpressionEvaluator evaluator;

    public VariableAssignment(ExpressionEvaluator evaluator, TreeBuilder head){
        super(head);
        this.evaluator = evaluator;
    }

    public VariableAssignment(ExpressionEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "variable_name equals * eol");
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {
        if(!isValid(tokens))
            return super.build(tokens);

        ASTNode root = new ASTNode(Token.EQUALS);

        int indexOfEOL = tokens.indexOf(Token.EOL);
        Result<ASTNode> exprRes = evaluator.buildTree(tokens.subList(2, indexOfEOL));

        if(exprRes.isFailure())
            return new TreeBuilderResult(exprRes, 0);

        root.addChildren(
                new ASTNode(tokens.get(0)),
                exprRes.get()
        );

        return new TreeBuilderResult(Result.success(root), indexOfEOL+1);
    }
}
