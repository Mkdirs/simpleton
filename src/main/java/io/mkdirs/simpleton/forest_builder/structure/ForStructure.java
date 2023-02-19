package io.mkdirs.simpleton.forest_builder.structure;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.forest_builder.TreeBuilder;
import io.mkdirs.simpleton.forest_builder.TreeBuilderResult;
import io.mkdirs.simpleton.model.token.EOL;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.result.Result;

import java.util.ArrayList;
import java.util.List;

public class ForStructure extends AbstractStructure {

    private ExpressionEvaluator evaluator;

    public ForStructure(ExpressionEvaluator evaluator, TreeBuilder head){
        super(head);
        this.evaluator = evaluator;

    }

    public ForStructure(ExpressionEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "for_kw l_paren * comma * comma * r_paren do_kw l_bracket eol");
    }

    private List<Token> getPart(int index, List<Token> tokens){
        List<Token> part = new ArrayList<>();
        int i = -1;
        for(Token token : tokens){

            if(TokenKind.COMMA.equals(token.kind)) {
                i++;
                if(index != i)
                    part.clear();
                else
                    break;

                continue;
            }

            part.add(token);
        }

        return part;
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
        var strippedLine = tokens.subList(2, firstLine.lastIndexOf(
                firstLine.stream()
                        .filter(e -> TokenKind.R_PAREN.equals(e.kind))
                        .findFirst().orElse(null))
        );

        var initialization = getPart(0, strippedLine);
        initialization.add(new EOL());

        var condition = getPart(1, strippedLine);
        condition.add(new EOL());

        var loopStatement = getPart(2, strippedLine);
        loopStatement.add(new EOL());

        var initRes = head.build(initialization);
        if(initRes.tree().isFailure())
            return initRes;

        var conditionRes = head.build(condition);
        if(conditionRes.tree().isFailure())
            return conditionRes;

        var loopStatementRes = head.build(loopStatement);
        if(loopStatementRes.tree().isFailure())
            return loopStatementRes;



        int jmp = indexOfEOL+1;
        TreeBuilderResult bodyResult = buildBody(tokens.subList(jmp, tokens.size()));

        if(bodyResult.tree().isFailure())
            return bodyResult;

        var bodyStatements = bodyResult.tree().get();
        bodyStatements.addChild(loopStatementRes.tree().get());

        jmp += bodyResult.jumpIndex();
        root.addChildren(
                initRes.tree().get(),
                conditionRes.tree().get(),
                bodyResult.tree().get()
        );



        return new TreeBuilderResult(Result.success(root), jmp);

    }
}
