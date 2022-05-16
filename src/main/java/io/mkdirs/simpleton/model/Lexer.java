package io.mkdirs.simpleton.model;

import io.mkdirs.simpleton.model.token.Token;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public List<Token> parse(String text){
        List<Token> tokens = new ArrayList<>();

        String[] lines = text.split("\n");
        int lineIndex = 0;
        boolean reading = lineIndex < lines.length;

        while(reading){
            String line = lines[lineIndex];

            

            lineIndex++;
            reading = lineIndex < lines.length;
        }

        return tokens;
    }
}
