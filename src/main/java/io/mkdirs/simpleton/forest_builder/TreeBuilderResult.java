package io.mkdirs.simpleton.forest_builder;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.model.error.StackableError;
import io.mkdirs.simpleton.result.Result;

public record TreeBuilderResult(Result<ASTNode, StackableError> tree, int jumpIndex) {}
