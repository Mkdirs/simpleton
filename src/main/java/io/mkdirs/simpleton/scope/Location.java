package io.mkdirs.simpleton.scope;

import io.mkdirs.simpleton.evaluator.ASTNode;

public class Location {
    private final ASTNode body;

    public Location(ASTNode body){
        this.body = body;
    }

    public ASTNode getBody() {
        return body;
    }

    public static final Location BUILTINS = new Location(null);
}
