package io.mkdirs.simpleton.forest_builder.structure;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.TreeBuilder;
import io.mkdirs.simpleton.forest_builder.TreeBuilderResult;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public class WhileStructure extends AbstractStructure {

    private ExpressionEvaluator evaluator;

    public WhileStructure(ExpressionEvaluator evaluator, TreeBuilder head){
        super(head);
        this.evaluator = evaluator;

    }

    public WhileStructure(ExpressionEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "while_kw l_paren * r_paren do_kw l_bracket eol");
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {
        if(!isValid(tokens))
            return super.build(tokens);

        ASTNode root = new ASTNode(tokens.get(0));

        Result<ASTNode> exprRes = evaluator.buildTree(tokens.subList(2, tokens.indexOf(
                tokens.stream()
                        .filter(e -> TokenKind.R_PAREN.equals(e.kind))
                        .findFirst().orElse(null)
        )));

        if(exprRes.isFailure())
            return new TreeBuilderResult(exprRes, 0);

        int indexOfEOL = tokens.indexOf(
                tokens.stream()
                        .filter(e -> TokenKind.EOL.equals(e.kind))
                        .findFirst().orElse(null)
        );
        int jmp = indexOfEOL+1;
        TreeBuilderResult bodyResult = buildBody(tokens.subList(jmp, tokens.size()));

        if(bodyResult.tree().isFailure())
            return bodyResult;

        jmp += bodyResult.jumpIndex();
        root.addChildren(exprRes.get(), bodyResult.tree().get());



        return new TreeBuilderResult(Result.success(root), jmp);

    }
}
