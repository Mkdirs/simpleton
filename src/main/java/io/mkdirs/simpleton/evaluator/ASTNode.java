package io.mkdirs.simpleton.evaluator;

import io.mkdirs.simpleton.model.token.Token;

import java.util.*;
import java.util.stream.Collectors;

public class ASTNode {

    private final Token token;

    private List<ASTNode> children = new ArrayList<>();
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

    public void addChildren(ASTNode...nodes){
        addChildren(List.of(nodes));
    }

    public List<ASTNode> getChildren(){return Collections.unmodifiableList(children);}

    public ASTNode get(int index){
        if(index < 0 || index >= children.size())
            return null;

        return children.get(index);
    }

    public ASTNode left() {
        if(children.isEmpty())
            return null;

        return get(0);
    }

    public ASTNode right() {
        if(children.size() < 2)
            return null;

        return get(1);
    }

    public boolean isLeaf(){return this.children.isEmpty();}

    @Override
    public String toString() {
        if(isLeaf())
            return token == null ? "." : token.toString();
        else {
            return token.toString() + "{" + children.stream().map(ASTNode::toString)/*.collect(Collectors.joining())*/ + "}";
        }
    }
}
