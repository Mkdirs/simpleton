package io.mkdirs.simpleton.forest_builder;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.result.Result;

public record TreeBuilderResult(Result<ASTNode> tree, int jumpIndex) {}
