package io.mkdirs.simpleton.lexer;

import io.mkdirs.simpleton.model.token.*;
import io.mkdirs.simpleton.model.token.composite.*;
import io.mkdirs.simpleton.model.token.keword.*;
import io.mkdirs.simpleton.model.token.literal.*;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.result.ResultProvider;
import io.mkdirs.simpleton.scope.ScopeContext;

import java.util.*;

public class Lexer extends ResultProvider {

    private int charIndex = 0;



    public Result<List<Token>> parse(String statement) {
        setStatement(statement);

        List<Token> tokens = new ArrayList<>();
        this.charIndex = 0;

        char[] chars = statement.toCharArray();


        while(this.charIndex < chars.length){

            Result<LexerSubParsingResult> result = null;

            Character car = chars[charIndex];

            if(Character.isWhitespace(car)){
                this.charIndex++;
                continue;
            }


            var charMatch = Arrays.stream(TokenKind.values())
                    .filter(TokenKind::isSingleChar)
                    .filter(e -> !e.isKeyword())
                    .filter(e -> car.equals(e.literal.charAt(0)))
                    .findFirst();


            if(charMatch.isPresent()) {
                TokenKind kind = charMatch.get();
                switch (kind){
                    case AMPERSAND -> tokens.add(new Ampersand());
                    case EQUALS -> tokens.add(new Equals());
                    case GREATER_THAN -> tokens.add(new GreaterThan());
                    case LOWER_THAN -> tokens.add(new LowerThan());
                    case NOT -> tokens.add(new Not());
                    case PIPE -> tokens.add(new Pipe());
                    case COLON -> tokens.add(new Colon());
                    case COMMA -> tokens.add(new Comma());
                    case DIVIDE -> tokens.add(new Divide());
                    case EOL -> tokens.add(new EOL());
                    case L_BRACKET -> tokens.add(new LBracket());
                    case R_BRACKET -> tokens.add(new RBracket());
                    case L_PAREN -> tokens.add(new LParen());
                    case R_PAREN -> tokens.add(new RParen());
                    case MINUS -> tokens.add(new Minus());
                    case PLUS -> tokens.add(new Plus());
                    case STAR -> tokens.add(new Star());
                    case PERCENT -> tokens.add(new Percent());
                }

                charIndex += (kind.literal.length());
                continue;

            }else{
                var textualMatch = Arrays.stream(TokenKind.values())
                        .filter(TokenKind::isKeyword)
                        .filter(e -> isText(e.literal))
                        .findFirst();

                if(textualMatch.isPresent()){
                    TokenKind kind = textualMatch.get();
                    switch (kind){
                        case BOOL_KW -> tokens.add(new BoolKW());
                        case CHAR_KW -> tokens.add(new CharKW());
                        case STRING_KW -> tokens.add(new StringKW());
                        case INT_KW -> tokens.add(new IntKW());
                        case FLOAT_KW -> tokens.add(new FloatKW());
                        case ANY_KW -> tokens.add(new AnyKW());
                        case DEF_KW -> tokens.add(new DefKW());
                        case DO_KW -> tokens.add(new DoKW());
                        case ELSE_KW -> tokens.add(new ElseKW());
                        case FUNCTION_KW -> tokens.add(new FunctionKW());
                        case IF_KW -> tokens.add(new IfKW());
                        case LET_KW -> tokens.add(new LetKW());
                        case NULL_KW -> tokens.add(new NullKW());
                        case RETURN_KW -> tokens.add(new ReturnKW());
                        case THEN_KW -> tokens.add(new ThenKW());
                        case VOID_KW -> tokens.add(new VoidKW());
                        case WHILE_KW -> tokens.add(new WhileKW());
                    }

                    charIndex += (kind.literal.length());
                    continue;

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
                if(result.isSuccess()){
                    tokens.add(result.get().getToken());
                    this.charIndex+=result.get().getCharsToSkip();
                    continue;
                }else{
                    return Result.failure(result.getMessage());
                }
            }



            this.charIndex++;
        }

        return buildFuncTokens(collapse(tokens));
    }

    private Result<Func> buildFunc(List<Token> tokens){
        if(!TokenKind.FUNC.equals(tokens.get(0).kind))
            return null;

        Func func = (Func) tokens.get(0);
        func.addInBody(new LParen());
        int openParen = 1;
        boolean finished = false;
        boolean lonelyComma = false;
        int i = 1;
        while(i< tokens.size() && !finished){
            Token current = tokens.get(i);

            if(TokenKind.L_PAREN.equals(current.kind)) {
                openParen++;
                i++;
                func.addInBody(current);
                continue;
            }

            if(TokenKind.R_PAREN.equals(current.kind)){
                openParen--;

                if(openParen <= 0){
                    finished = true;
                }

                func.addInBody(current);

                i++;
                continue;
            }


            if(TokenKind.FUNC.equals(current.kind)){
                Result<Func> otherFuncRes = buildFunc(tokens.subList(i, tokens.size()));
                if(otherFuncRes.isFailure())
                    return otherFuncRes;

                func.addInBody(otherFuncRes.get());
                i+= otherFuncRes.get().getFullBodyLength();
                continue;
            }else if(TokenKind.COMMA.equals(current.kind)) {
                func.addInBody(current);
                lonelyComma = (TokenKind.COMMA.equals(tokens.get(i-1).kind) || i-1 == 0) || (i+1 == tokens.size()-1 || TokenKind.COMMA.equals(tokens.get(i+1).kind));

                if(lonelyComma)
                    finished = true;
            }else {
                func.addInBody(current);
            }

            i++;

        }

        //lonelyComma = func.getBody().isEmpty() ? lonelyComma || false : lonelyComma;

        if(lonelyComma)
            return pushError("Error lonely comma detected");

        if(openParen > 0)
            return pushError("Error unclosed parenthesis");


        return Result.success(func);
    }
    private Result<List<Token>> buildFuncTokens(List<Token> tokens){
        List<Token> result = new ArrayList<>();
        int i = 0;
        while(i < tokens.size()) {
            Token current = tokens.get(i);

            if (TokenKind.FUNC.equals(current.kind)) {
                Result<Func> funcRes = buildFunc(tokens.subList(i, tokens.size()));

                if(funcRes.isFailure())
                    return Result.failure(funcRes.getMessage());

                result.add(funcRes.get());
                i += funcRes.get().getFullBodyLength();
            }else {
                result.add(current);
                i++;
            }
        }

        return Result.success(result);
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


                if(current instanceof IComposable){
                    Token collapsedTk = ((IComposable) current).compose(next);
                    if(collapsedTk != null) {
                        collapsed.add(collapsedTk);
                        ignoreNext = true;
                        continue;
                    }
                }

                collapsed.add(current);

            }

        }
        collapsed.add(tokens.get(tokens.size()-1));

        return collapsed;
    }


    private boolean isText(String s){
        if(this.charIndex+s.length() > this.statement.length())
            return false;

        String extracted = "";
        int offst = this.charIndex;
        while(offst < this.statement.length()){
            Character c = this.statement.charAt(offst);

            boolean metToken = Arrays.stream(TokenKind.values())
                    .filter(TokenKind::hasLiteral)
                    .filter(e -> !e.isKeyword())
                    .map(e -> e.literal)
                    .anyMatch(e -> c.equals(e.charAt(0)));

            if(Character.isWhitespace(c) || metToken)
                break;

            extracted+=c;

            offst++;
        }

        //String extracted = this.scopeContext.getLine().substring(this.charIndex, this.charIndex+s.length());

        return extracted.equals(s);
    }


    private Character seekTo(int charIndex){
        if(charIndex < 0 || charIndex >= this.statement.length())
            return Character.UNASSIGNED;

        return this.statement.charAt(charIndex);
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

    private Result<LexerSubParsingResult> parseBoolean(){
        final String boolean_true = "true";
        final String boolean_false = "false";

        if(isText(boolean_true))
            return Result.success(new LexerSubParsingResult(new BooleanLiteral(boolean_true), boolean_true.length()));

        else if(isText(boolean_false))
            return Result.success(new LexerSubParsingResult(new BooleanLiteral(boolean_false), boolean_false.length()));

        return pushError("Unexpected error at parseBoolean", this.charIndex);
    }

    private Result<LexerSubParsingResult> parseVariableName(){

        String name = "";
        int offset = 0;
        //TODO: Use regex
        while(Character.isLetterOrDigit(seekTo(this.charIndex+offset)) && !seekTo(this.charIndex+offset).equals(' ') && seekTo(this.charIndex+offset) != Character.LINE_SEPARATOR && seekTo(this.charIndex+offset) != Character.UNASSIGNED){
            name += seekTo(this.charIndex+offset);
            offset++;
        }

        if(name.isEmpty())
            return pushError("Unexpected error at parseVariableName", this.charIndex);

        LexerSubParsingResult result = new LexerSubParsingResult(new VariableName(name), offset);
        return Result.success(result);
    }

    private Result<LexerSubParsingResult> parseCharacter(){
        //Begin
        if(!seekTo(this.charIndex).equals('\'')) {
            return pushError("Unexpected error at parseCharacter", this.charIndex, 1);
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
            return pushError("Unclosed character literal", this.charIndex+offset, 1);
        }

        if(value.length() == 0){
            return pushError("Empty character literal", this.charIndex, 2);
        }

        if(value.length() == 2 && value.startsWith("\\")){
            value = "\\"+value.charAt(1);

        }else if(value.length() > 1){
            return pushError("Too many characters in character literal", this.charIndex, offset+1);
        }


        LexerSubParsingResult result = new LexerSubParsingResult(new CharacterLiteral(value), offset+1);
        return Result.success(result);
    }

    private Result<LexerSubParsingResult> parseString(){
        //Begin
        if(!seekTo(this.charIndex).equals('\"')) {
            return pushError("Unexpected error at parseString", this.charIndex, 1);
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
            return pushError("Unclosed string literal", this.charIndex+offset, 1);
        }

        LexerSubParsingResult result = new LexerSubParsingResult(new StringLiteral(string), offset+1);
        return Result.success(result);
    }

    private Result<LexerSubParsingResult> parseNumber(){
        if(!Character.isDigit(seekTo(this.charIndex))){
            return pushError("Unexpected error at parseNumber", this.charIndex, 1);
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
            type = new IntegerLiteral(rawValue);
        else
            type = new FloatLiteral(rawValue);

        LexerSubParsingResult result = new LexerSubParsingResult(type, offset);

        return Result.success(result);
    }

}
