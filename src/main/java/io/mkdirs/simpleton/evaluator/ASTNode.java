package io.mkdirs.simpleton.evaluator;

import io.mkdirs.simpleton.model.token.Token;

public class ASTNode {

    private final Token token;
    private final ASTNode left;
    private final ASTNode right;

    public ASTNode(ASTNode left, Token token, ASTNode right){
        this.left = left;
        this.token = token;
        this.right = right;
    }

    public Token getToken() {
        return token;
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }

    public boolean isLeaf(){return this.left == null && this.right == null;}
}
