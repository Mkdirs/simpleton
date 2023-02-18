package io.mkdirs.simpleton.forest_builder.structure;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.TreeBuilder;
import io.mkdirs.simpleton.forest_builder.TreeBuilderResult;
import io.mkdirs.simpleton.forest_builder.structure.AbstractStructure;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public class IfStructure extends AbstractStructure {

    private ExpressionEvaluator evaluator;
    private final ElseStructure elseStructure;

    public IfStructure(ExpressionEvaluator evaluator, TreeBuilder head){
        super(head);
        this.evaluator = evaluator;
        elseStructure = new ElseStructure(this.evaluator, head);

    }

    public IfStructure(ExpressionEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
        elseStructure = new ElseStructure(this.evaluator);
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "if_kw l_paren * r_paren then_kw l_bracket eol");
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

        var firstLine = tokens.subList(0, indexOfEOL);

        Result<ASTNode> exprRes = evaluator.buildTree(firstLine.subList(2, firstLine.lastIndexOf(
                firstLine.stream()
                        .filter(e -> TokenKind.R_PAREN.equals(e.kind))
                        .findFirst().orElse(null)
        )));

        if(exprRes.isFailure())
            return new TreeBuilderResult(exprRes, 0);


        int jmp = indexOfEOL+1;
        TreeBuilderResult bodyResult = buildBody(tokens.subList(jmp, tokens.size()));

        if(bodyResult.tree().isFailure())
            return bodyResult;

        jmp += bodyResult.jumpIndex();
        root.addChildren(exprRes.get(), bodyResult.tree().get());


        if(elseStructure.isValid(tokens.subList(jmp, tokens.size()))){
            TreeBuilderResult elseResult = elseStructure.build(tokens.subList(jmp, tokens.size()));

            if(elseResult.tree().isFailure())
                return elseResult;

            jmp += elseResult.jumpIndex();
            root.addChild(elseResult.tree().get());
        }


        return new TreeBuilderResult(Result.success(root), jmp);

    }
}
