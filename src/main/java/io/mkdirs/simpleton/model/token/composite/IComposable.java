package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

public interface IComposable {

    public Token compose(Token token);
}
