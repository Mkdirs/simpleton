package io.mkdirs.simpleton.lexer;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.scope.ScopeContext;

import java.util.*;

public class Lexer {

    private final ScopeContext scopeContext;
    private int charIndex = -1;

    public Lexer(ScopeContext scopeContext){
        this.scopeContext = scopeContext;
    }

    public List<Token> parse() {
        List<Token> tokens = new ArrayList<>();


        char[] chars = this.scopeContext.getLine().toCharArray();
        this.charIndex = 0;
        boolean skip = false;

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
                if(literal.length() > 1)
                    charIndex+=(literal.length()-1);
            }else if(Character.isDigit(car)) {
                result = parseNumber();
            }else if(isText("true") || isText("false")) {
                result = parseBoolean();
            }else if(car.equals('\'')) {
                result = parseCharacter();
            }else if(car.equals('\"')){
                result = parseString();
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
                    skip = true;
                    this.scopeContext.pushError("Unknown character", this.charIndex, 1);
                    //pushError("Unknown character", this.charIndex, 1);
                }
            }

            if(skip)
                break;


            this.charIndex++;
        }


        tokens.add(Token.END_OF_LINE);

        return collapse(tokens);
    }

    private List<Token> collapse(List<Token> tokens){
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


    private boolean isText(String s){
        if(this.charIndex+s.length() > this.scopeContext.getLine().length())
            return false;

        String extracted = this.scopeContext.getLine().substring(this.charIndex, this.charIndex+s.length());

        return extracted.equals(s);
    }


    private Character seekTo(int charIndex){
        if(charIndex < 0 || charIndex >= this.scopeContext.getLine().length())
            return Character.UNASSIGNED;

        return this.scopeContext.getLine().charAt(charIndex);
    }


    /*private void pushError(String message, int charStartIndex, int selectionSize){
        StringBuilder builder = new StringBuilder()
                .append(message)
                .append("\n")
                .append("\t").append(this.line)
                .append("\n\t")
                .append(" ".repeat(charStartIndex)).append("^".repeat(selectionSize));

        this.errorStack.add(0, builder.toString());
    }

     */


    /*private void pushError(String message, int charStartIndex){
        pushError(message, charStartIndex, this.line.length()-charStartIndex);
    }

     */

    private Optional<LexerSubParsingResult> parseBoolean(){
        final String boolean_true = "true";
        final String boolean_false = "false";

        if(isText(boolean_true))
            return Optional.of(new LexerSubParsingResult(Token.BOOLEAN_LITERAL.with(boolean_true), boolean_true.length()-1));

        else if(isText(boolean_false))
            return Optional.of(new LexerSubParsingResult(Token.BOOLEAN_LITERAL.with(boolean_false), boolean_false.length()-1));

        return Optional.empty();
    }

    private Optional<LexerSubParsingResult> parseVariableName(){

        String name = "";
        int offset = 0;
        //TODO: Use regex
        while(Character.isLetterOrDigit(seekTo(this.charIndex+offset)) && !seekTo(this.charIndex+offset).equals(' ') && !seekTo(this.charIndex+offset).equals(Character.LINE_SEPARATOR) && !seekTo(this.charIndex+offset).equals(Character.UNASSIGNED)){
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
        if(!seekTo(this.charIndex).equals('\'')) {
            this.scopeContext.pushError("Unexpected error", this.charIndex, 1);
            return Optional.empty();
        }

        int offset = 1;
        String value = "";
        boolean closed = false;
        while(!seekTo(this.charIndex+offset).equals('\'') && seekTo(this.charIndex+offset) != Character.LINE_SEPARATOR && seekTo(this.charIndex+offset) != Character.UNASSIGNED){

            value += seekTo(this.charIndex+offset);
            offset++;
        }

        if(seekTo(this.charIndex+offset).equals('\''))
            closed = true;

        if(!closed){
            this.scopeContext.pushError("Unclosed character literal", this.charIndex+offset, 1);
            return Optional.empty();
        }

        if(value.length() == 0){
            this.scopeContext.pushError("Empty character literal", this.charIndex, 2);
            return Optional.empty();
        }

        if(value.length() > 1){
            this.scopeContext.pushError("Too many characters in character literal", this.charIndex, offset+1);
            return Optional.empty();
        }


        LexerSubParsingResult result = new LexerSubParsingResult(Token.CHARACTER_LITERAL.with(value), offset);
        return Optional.of(result);
    }

    private Optional<LexerSubParsingResult> parseString(){
        //Begin
        if(!seekTo(this.charIndex).equals('\"')) {
            this.scopeContext.pushError("Unexpected error", this.charIndex, 1);
            return Optional.empty();
        }

        boolean closed = false;
        int offset = 1;
        String string = "";

        while(seekTo(this.charIndex+offset) != '\"' && seekTo(this.charIndex+offset) != Character.LINE_SEPARATOR && seekTo(this.charIndex+offset) != Character.UNASSIGNED){
            string += seekTo(this.charIndex+offset);
            offset++;
        }


        if(seekTo(this.charIndex+offset).equals('\"'))
            closed = true;

        if(!closed){
            this.scopeContext.pushError("Unclosed string literal", this.charIndex+offset, 1);
            return Optional.empty();
        }

        LexerSubParsingResult result = new LexerSubParsingResult(Token.STRING_LITERAL.with(string), offset);
        return Optional.of(result);
    }

    private Optional<LexerSubParsingResult> parseNumber(){
        if(!Character.isDigit(seekTo(this.charIndex))){
            this.scopeContext.pushError("Unexpected error", this.charIndex, 1);
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


        Token type = null;
        if(isInteger)
            type = Token.INTEGER_LITERAL;
        else
            type = Token.FLOAT_LITERAL;

        LexerSubParsingResult result = new LexerSubParsingResult(type.with(rawValue), offset-1);

        return Optional.of(result);
    }

}
