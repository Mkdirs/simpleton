package io.mkdirs.simpleton.forest_builder;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenKind;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public class VariableDeclaration extends TreeBuilder{

    public VariableDeclaration(TreeBuilder head) {
        super(head);
    }

    @Override
    protected boolean isValid(List<Token> tokens) {
        return Simpleton.match(tokens, "let_kw var_name colon type eol");
    }

    @Override
    public TreeBuilderResult build(List<Token> tokens) {
        if(!isValid(tokens))
            return super.build(tokens);

        ASTNode root = new ASTNode(tokens.get(0));

        root.addChildren(
                new ASTNode(tokens.get(1)),
                new ASTNode(tokens.get(3))
        );

        int indexOfEOL = tokens.indexOf(
                tokens.stream()
                        .filter(e -> TokenKind.EOL.equals(e.kind))
                        .findFirst().orElse(null)
        );
        return new TreeBuilderResult(Result.success(root), indexOfEOL+1);
    }
}
