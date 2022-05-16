package io.mkdirs.simpleton.model.lexer;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.TokenType;

import java.util.Optional;

public class LexerSubParsingResult {

    private Token token;
    private Integer charsToSkip;



    public LexerSubParsingResult(Token token, Integer charsToSkip){
        this.token = token;
        this.charsToSkip = charsToSkip;
    }

    public Token getToken() {
        return this.token;
    }

    public Integer getCharsToSkip() {
        return this.charsToSkip;
    }
}
