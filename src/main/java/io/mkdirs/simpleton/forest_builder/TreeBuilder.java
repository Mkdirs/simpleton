package io.mkdirs.simpleton.forest_builder;

import com.sun.source.tree.Tree;
import io.mkdirs.simpleton.model.token.Token;

import java.util.List;

public abstract class TreeBuilder {

    protected TreeBuilder next;
    protected TreeBuilder head;

    protected TreeBuilder(TreeBuilder head){
        this.head = head;
    }

    protected  TreeBuilder(){
        this.head = this;
    }

    public TreeBuilder next(TreeBuilder next){
        this.next = next;
        return this.next;
    }

    protected abstract boolean isValid(List<Token> tokens);
    public TreeBuilderResult build(List<Token> tokens){
        if(next != null)
            return next.build(tokens);

        else return null;
    }
}
