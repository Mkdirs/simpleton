package io.mkdirs.simpleton.evaluator;

import io.mkdirs.simpleton.model.token.Token;

import java.util.*;

public class ASTNode {

    private final Token token;

    private List<ASTNode> children = new LinkedList<>();
    //private final ASTNode left;
    //private final ASTNode right;

    public ASTNode(Token token){
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void addChild(ASTNode child){
        children.add(child);
    }

    public void addChildren(Collection<ASTNode> children){
        this.children.addAll(children);
    }

    public ASTNode left() {
        if(children.isEmpty())
            return null;

        return children.get(0);
    }

    public ASTNode right() {
        if(children.size() < 2)
            return null;

        return children.get(1);
    }

    public boolean isLeaf(){return this.children.isEmpty();}

    @Override
    public String toString() {
        if(isLeaf())
            return token.toString();
        else
            return token.toString()+"{"+ Arrays.toString(children.stream().map(ASTNode::toString).toArray())+"}";
    }
}
