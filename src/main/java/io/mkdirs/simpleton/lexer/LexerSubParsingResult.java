package io.mkdirs.simpleton.lexer;

import io.mkdirs.simpleton.model.token.Token;

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
