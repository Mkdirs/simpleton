package io.mkdirs.simpleton.lexer;

import io.mkdirs.simpleton.model.token.Token;

import java.util.*;
import java.util.function.Function;

public class Lexer {

    private String text;

    private String[] lines;
    private int lineIndex = -1;
    private int charIndex = -1;

    private List<String> errorStack = new ArrayList<>();

    public Lexer(String text){
        this.text = text;
        this.lines = text.split("\n");
    }

    public List<Token> parse() {
        List<Token> tokens = new ArrayList<>();

        this.lineIndex = 0;
        boolean reading = lineIndex < lines.length;

        while(reading){
            String line = this.lines[lineIndex];
            char[] chars = line.toCharArray();
            this.charIndex = 0;
            boolean skipLine = false;
            while(this.charIndex < chars.length){

                Optional<LexerSubParsingResult> result = null;

                Character car = chars[charIndex];

                if(Character.isWhitespace(car)){
                    this.charIndex++;
                    continue;
                }

                var literalMatch = Token.values().stream().map(Token::getLiteral)
                        .filter(Objects::nonNull)
                        .filter(this::isText)
                        .findFirst();

                if(literalMatch.isPresent()){
                    String literal = literalMatch.get();
                    tokens.add(Token.values().stream().filter(token -> literal.equals(token.getLiteral()) ).findFirst().get());
                    charIndex+=(literal.length()-1);
                }else if(Character.isDigit(car)){
                    result = parseNumber();
                }else{
                    result = parseVariableName();
                }




                /*switch(car){
                    case '+':
                        tokens.add(Token.PLUS);
                        break;

                    case '-':
                        tokens.add(new Token(Token.MINUS));
                        break;

                    case '*':
                        tokens.add(new Token(Token.TIMES));
                        break;

                    case '/':
                        tokens.add(new Token(Token.DIVIDE));
                        break;

                    case '>':
                        tokens.add(new Token(Token.GREATER_THAN));
                        break;

                    case '<':
                        tokens.add(new Token(Token.SMALLER_THAN));
                        break;

                    case '(':
                        tokens.add(new Token(Token.LEFT_PARENTHESIS));
                        break;

                    case ')':
                        tokens.add(new Token(Token.RIGHT_PARENTHESIS));
                        break;

                    case '!':
                        if(isText("!=")){
                            tokens.add(new Token(Token.INEQUALITY));
                            this.charIndex++;
                        }else
                            pushError("Unexpected character '!'", this.charIndex+1, 1);

                        break;

                    case '=':
                        if(isText("==")) {
                            tokens.add(new Token(Token.EQUALITY));
                            this.charIndex++;
                        }else
                            tokens.add(new Token(Token.ASSIGN));

                        break;

                    case '&':
                        if(isText("&&")) {
                            tokens.add(new Token(Token.AND));
                            this.charIndex++;
                        }else{
                            pushError("Expected character '&'", this.charIndex+1, 1);
                        }

                        break;

                    case '|':
                        if(isText("||")){
                            tokens.add(new Token(Token.OR));
                            this.charIndex++;
                        }else{
                            pushError("Expected character '|'", this.charIndex+1, 1);
                        }

                        break;

                    case '\'':
                        result = parseCharacter();
                        break;
                    case '\"':
                        result = parseString();
                        break;
                    default:

                        if(Character.isDigit(car)) {
                            result = parseNumber();
                        }else if(isText("true")) {
                            tokens.add(new Token(Token.BOOLEAN_LITERAL, "true"));
                            this.charIndex+=3;
                        }else if(isText("false")) {
                            tokens.add(new Token(Token.BOOLEAN_LITERAL, "false"));
                            this.charIndex += 4;
                        }else if(isText("int")) {
                            tokens.add(new Token(Token.INT_KW));
                            this.charIndex += 2;
                        }else if(isText("float")) {
                            tokens.add(new Token(Token.FLOAT_KW));
                            this.charIndex += 4;
                        }else if(isText("string")) {
                            tokens.add(new Token(Token.STRING_KW));
                            this.charIndex += 5;
                        }else if(isText("char")) {
                            tokens.add(new Token(Token.CHAR_KW));
                            this.charIndex += 3;
                        }else if(isText("bool")){
                            tokens.add(new Token(Token.BOOL_KW));
                            this.charIndex+=3;
                        }else{
                            result = parseVariableName();
                        }
                        break;
                }

                 */

                if(result != null){
                    if(result.isPresent()){
                        tokens.add(result.get().getToken());
                        this.charIndex+=result.get().getCharsToSkip();
                    }else if(result.isEmpty()){
                        skipLine = true;
                        pushError("Unknown character", this.charIndex, 1);
                    }
                }

                if(skipLine)
                    break;


                this.charIndex++;
            }


            this.lineIndex++;
            tokens.add(Token.END_OF_LINE);
            reading = this.lineIndex < this.lines.length;
        }

        return collapse(tokens);
    }

    public List<Token> collapse(List<Token> tokens){
        List<Token> collapsed = new ArrayList<>();
        boolean ignoreNext = false;

        for(int i = 0; i < tokens.size()-1; i++){
            if(ignoreNext){
                ignoreNext = false;
            }else{
                Token current = tokens.get(i);
                Token next = tokens.get(i+1);

                if(current.canCollapseFrom(next)) {
                    collapsed.add(current.getCollapsedForm());
                    ignoreNext = true;
                }else
                    collapsed.add(current);
            }

        }

        return collapsed;
    }

    public List<String> getErrorStack(){
        return this.errorStack;
    }

    private boolean isText(String s){
        if(this.charIndex+s.length() > this.lines[this.lineIndex].length())
            return false;

        String extracted = this.lines[this.lineIndex].substring(this.charIndex, this.charIndex+s.length());

        return extracted.equals(s);
    }


    private Character seekTo(int lineIndex, int charIndex){
        if(lineIndex < 0 || lineIndex >= this.lines.length)
            return Character.UNASSIGNED;
        if(charIndex < 0 || charIndex >= this.lines[lineIndex].length())
            return Character.UNASSIGNED;

        return this.lines[lineIndex].charAt(charIndex);
    }

    private Character seekTo(int charIndex){
        return seekTo(this.lineIndex, charIndex);
    }

    private void pushError(String message, int charStartIndex, int selectionSize){
        StringBuilder builder = new StringBuilder()
                .append(message).append(" at line "+(lineIndex+1))
                .append("\n")
                .append("\t").append(this.lines[lineIndex])
                .append("\n\t")
                .append(" ".repeat(charStartIndex)).append("^".repeat(selectionSize));

        this.errorStack.add(0, builder.toString());
    }


    private void pushError(String message, int charStartIndex){
        pushError(message, charStartIndex, this.lines[this.lineIndex].length()-charStartIndex);
    }

    private Optional<LexerSubParsingResult> parseVariableName(){

        String name = "";
        int offset = 0;
        //TODO: Use regex
        while(Character.isLetterOrDigit(seekTo(this.charIndex+offset)) && seekTo(this.charIndex+offset) != ' ' && seekTo(this.charIndex+offset) != Character.LINE_SEPARATOR && seekTo(this.charIndex+offset) != Character.UNASSIGNED){
            name += seekTo(this.charIndex+offset);
            offset++;
        }

        if(name.isEmpty())
            return Optional.empty();

        LexerSubParsingResult result = new LexerSubParsingResult(Token.VARIABLE_NAME.with(name), offset-1);
        return Optional.of(result);
    }

    private Optional<LexerSubParsingResult> parseCharacter(){
        //Begin
        if(seekTo(this.charIndex) != '\'') {
            pushError("Unexpected error", this.charIndex, 1);
            return Optional.empty();
        }

        int offset = 1;
        String value = "";
        boolean closed = false;
        while(seekTo(this.charIndex+offset) != '\'' && seekTo(this.charIndex+offset) != Character.LINE_SEPARATOR && seekTo(this.charIndex+offset) != Character.UNASSIGNED){

            value += seekTo(this.charIndex+offset);
            offset++;
        }

        if(seekTo(this.charIndex+offset) == '\'')
            closed = true;

        if(!closed){
            pushError("Unclosed character literal", this.charIndex+offset, 1);
            return Optional.empty();
        }

        if(value.length() == 0){
            pushError("Empty character literal", this.charIndex, 2);
            return Optional.empty();
        }

        if(value.length() > 1){
            pushError("Too many characters in character literal", this.charIndex, offset+1);
            return Optional.empty();
        }


        LexerSubParsingResult result = new LexerSubParsingResult(Token.CHARACTER_LITERAL.with(value), offset);
        return Optional.of(result);
    }

    private Optional<LexerSubParsingResult> parseString(){
        //Begin
        if(seekTo(this.charIndex) != '\"') {
            pushError("Unexpected error", this.charIndex, 1);
            return Optional.empty();
        }

        boolean closed = false;
        int offset = 1;
        String string = "";

        while(seekTo(this.charIndex+offset) != '\"'&& seekTo(this.charIndex+offset) != Character.LINE_SEPARATOR && seekTo(this.charIndex+offset) != Character.UNASSIGNED){

            string += seekTo(this.charIndex+offset);
            offset++;
        }


        if(seekTo(this.charIndex+offset) == '\"')
            closed = true;

        if(!closed){
            pushError("Unclosed string literal", this.charIndex+offset, 1);
            return Optional.empty();
        }

        LexerSubParsingResult result = new LexerSubParsingResult(Token.STRING_LITERAL.with(string), offset);
        return Optional.of(result);
    }

    private Optional<LexerSubParsingResult> parseNumber(){
        if(!Character.isDigit(seekTo(this.charIndex))){
            pushError("Unexpected error", this.charIndex, 1);
            return Optional.empty();
        }

        String rawValue = "";
        boolean isInteger = true;
        int offset = 0;

        while(Character.isDigit(seekTo(this.charIndex+offset))){
            rawValue += seekTo(this.charIndex+offset);
            offset++;

            if(seekTo(this.charIndex+offset) == '.') {
                isInteger = false;
                rawValue+='.';
                offset++;
            }
        }

        if(!isInteger){

        }

        Token type = null;
        if(isInteger)
            type = Token.INTEGER_LITERAL;
        else
            type = Token.FLOAT_LITERAL;

        LexerSubParsingResult result = new LexerSubParsingResult(type.with(rawValue), offset-1);

        return Optional.of(result);
    }

}
