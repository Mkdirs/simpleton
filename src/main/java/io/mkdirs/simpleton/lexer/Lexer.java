package io.mkdirs.simpleton.lexer;

import io.mkdirs.simpleton.model.error.ErrorStack;
import io.mkdirs.simpleton.model.error.StackableError;
import io.mkdirs.simpleton.model.error.StackableErrorBuilder;
import io.mkdirs.simpleton.model.token.*;
import io.mkdirs.simpleton.model.token.composite.*;
import io.mkdirs.simpleton.model.token.keword.*;
import io.mkdirs.simpleton.model.token.literal.*;
import io.mkdirs.simpleton.result.Result;

import java.util.*;

public class Lexer{

    private int charIndex = 0;
    private int line = 0;
    private final String text;

    public Lexer(String text){
        this.text = text;
    }



    public Result<List<Token>, StackableError> parse() {

        List<Token> tokens = new ArrayList<>();
        this.charIndex = 0;
        line = 0;

        char[] chars = text.toCharArray();


        while(this.charIndex < chars.length){

            Result<LexerSubParsingResult, StackableError> result = null;

            Character car = chars[charIndex];

            if(car.equals(TokenKind.EOL.literal.charAt(0))){
                tokens.add(new EOL(line, charIndex));
                line++;
                charIndex++;
                continue;
            }

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
                    case AMPERSAND -> tokens.add(new Ampersand(line, charIndex));
                    case EQUALS -> tokens.add(new Equals(line, charIndex));
                    case GREATER_THAN -> tokens.add(new GreaterThan(line, charIndex));
                    case LOWER_THAN -> tokens.add(new LowerThan(line, charIndex));
                    case NOT -> tokens.add(new Not(line, charIndex));
                    case PIPE -> tokens.add(new Pipe(line, charIndex));
                    case COLON -> tokens.add(new Colon(line, charIndex));
                    case COMMA -> tokens.add(new Comma(line, charIndex));
                    case DIVIDE -> tokens.add(new Divide(line, charIndex));
                    case L_BRACKET -> tokens.add(new LBracket(line, charIndex));
                    case R_BRACKET -> tokens.add(new RBracket(line, charIndex));
                    case L_PAREN -> tokens.add(new LParen(line, charIndex));
                    case R_PAREN -> tokens.add(new RParen(line, charIndex));
                    case MINUS -> tokens.add(new Minus(line, charIndex));
                    case PLUS -> tokens.add(new Plus(line, charIndex));
                    case STAR -> tokens.add(new Star(line, charIndex));
                    case PERCENT -> tokens.add(new Percent(line, charIndex));
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
                        case BOOL_KW -> tokens.add(new BoolKW(line, charIndex));
                        case CHAR_KW -> tokens.add(new CharKW(line, charIndex));
                        case STRING_KW -> tokens.add(new StringKW(line, charIndex));
                        case INT_KW -> tokens.add(new IntKW(line, charIndex));
                        case FLOAT_KW -> tokens.add(new FloatKW(line, charIndex));
                        case ANY_KW -> tokens.add(new AnyKW(line, charIndex));
                        case DEF_KW -> tokens.add(new DefKW(line, charIndex));
                        case DO_KW -> tokens.add(new DoKW(line, charIndex));
                        case ELSE_KW -> tokens.add(new ElseKW(line, charIndex));
                        case FUNCTION_KW -> tokens.add(new FunctionKW(line, charIndex));
                        case IF_KW -> tokens.add(new IfKW(line, charIndex));
                        case LET_KW -> tokens.add(new LetKW(line, charIndex));
                        case NULL_KW -> tokens.add(new NullKW(line, charIndex));
                        case RETURN_KW -> tokens.add(new ReturnKW(line, charIndex));
                        case THEN_KW -> tokens.add(new ThenKW(line, charIndex));
                        case VOID_KW -> tokens.add(new VoidKW(line, charIndex));
                        case WHILE_KW -> tokens.add(new WhileKW(line, charIndex));
                        case FOR_KW -> tokens.add(new ForKW(line, charIndex));
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




            if(result != null){
                if(result.isSuccess()){
                    tokens.add(result.get().getToken());
                    this.charIndex+=result.get().getCharsToSkip();
                    continue;
                }else{
                    return Result.failure(result.err());
                }
            }



            this.charIndex++;
        }

        return buildFuncTokens(collapse(tokens));
    }

    private Result<Func, StackableError> buildFunc(List<Token> tokens){
        if(!TokenKind.FUNC.equals(tokens.get(0).kind))
            return null;

        Func func = (Func) tokens.get(0);
        func.addInBody(new LParen(func.line, func.column+1));
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
                var otherFuncRes = buildFunc(tokens.subList(i, tokens.size()));
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

        //System.out.println(tokens.get(i-2).toText());
        lonelyComma = lonelyComma || TokenKind.COMMA.equals(tokens.get(i-2).kind);

        if(lonelyComma)
            return Result.failure(new StackableErrorBuilder("Cannot parse function: lonely comma detected")
                    .build()
            );

        if(openParen > 0)
            return Result.failure(new StackableErrorBuilder("Cannot parse function: unclosed parenthesis")
                    .build()
            );


        return Result.success(func);
    }
    private Result<List<Token>, StackableError> buildFuncTokens(List<Token> tokens){
        List<Token> result = new ArrayList<>();
        int i = 0;
        int l = 0;
        while(i < tokens.size()) {
            Token current = tokens.get(i);

            if(TokenKind.EOL.equals(current.kind))
                l++;

            if (TokenKind.FUNC.equals(current.kind)) {
                var funcRes = buildFunc(tokens.subList(i, tokens.size()));

                if(funcRes.isFailure())
                    return Result.failure(new StackableErrorBuilder(funcRes.err().message())
                            .withLine(l)
                            .withStatement("")
                            .build()
                    );

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

    private String currentStatement(){

        return text.split("\n")[line];
    }


    private boolean isText(String s){
        if(this.charIndex+s.length() > text.length())
            return false;

        String extracted = "";
        int offst = this.charIndex;
        while(offst < text.length()){
            Character c = text.charAt(offst);

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
        if(charIndex < 0 || charIndex >= text.length())
            return Character.UNASSIGNED;

        return text.charAt(charIndex);
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

    private Result<LexerSubParsingResult, StackableError> parseBoolean(){
        final String boolean_true = "true";
        final String boolean_false = "false";

        if(isText(boolean_true))
            return Result.success(new LexerSubParsingResult(new BooleanLiteral(boolean_true, line, charIndex), boolean_true.length()));

        else if(isText(boolean_false))
            return Result.success(new LexerSubParsingResult(new BooleanLiteral(boolean_false, line, charIndex), boolean_false.length()));


        return Result.failure(new StackableErrorBuilder("Cannot parse boolean: Unexpected error")
                .withLine(line)
                .withStatement(currentStatement())
                .withCursor(0)
                .build()
        );
    }

    private Result<LexerSubParsingResult, StackableError> parseVariableName(){

        String name = "";
        int offset = 0;
        //TODO: Use regex
        while(Character.isLetterOrDigit(seekTo(this.charIndex+offset)) && !seekTo(this.charIndex+offset).equals(' ') && seekTo(this.charIndex+offset) != Character.LINE_SEPARATOR && seekTo(this.charIndex+offset) != Character.UNASSIGNED){
            name += seekTo(this.charIndex+offset);
            offset++;
        }

        if(name.isEmpty())
            return Result.failure(new StackableErrorBuilder("Cannot parse variable name: Unexpected error")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(0)
                    .build()
            );

        LexerSubParsingResult result = new LexerSubParsingResult(new VariableName(name, line, charIndex), offset);
        return Result.success(result);
    }

    private Result<LexerSubParsingResult, StackableError> parseCharacter(){
        //Begin
        if(!seekTo(this.charIndex).equals('\'')) {
            return Result.failure(new StackableErrorBuilder("Cannot parse character: Unexpected error")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(0)
                    .build()
            );
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
            return Result.failure(new StackableErrorBuilder("Cannot parse character: Unclosed character literal")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(0)
                    .build()
            );
        }

        if(value.length() == 0){
            return Result.failure(new StackableErrorBuilder("Cannot parse character: Empty character literal")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(0)
                    .build()
            );
        }

        if(value.length() == 2 && value.startsWith("\\")){
            value = "\\"+value.charAt(1);

        }else if(value.length() > 1){
            return Result.failure(new StackableErrorBuilder("Cannot parse character: Too many characters in character literal")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(0)
                    .build()
            );
        }


        LexerSubParsingResult result = new LexerSubParsingResult(new CharacterLiteral(value, line, charIndex), offset+1);
        return Result.success(result);
    }

    private Result<LexerSubParsingResult, StackableError> parseString(){
        //Begin
        if(!seekTo(this.charIndex).equals('\"')) {
            return Result.failure(new StackableErrorBuilder("Cannot parse string: Unexpected error")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(0)
                    .build()
            );
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
            return Result.failure(new StackableErrorBuilder("Cannot parse string: Unclosed string literal")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(0)
                    .build()
            );
        }

        LexerSubParsingResult result = new LexerSubParsingResult(new StringLiteral(string, line, charIndex), offset+1);
        return Result.success(result);
    }

    private Result<LexerSubParsingResult, StackableError> parseNumber(){
        if(!Character.isDigit(seekTo(this.charIndex))){
            return Result.failure(new StackableErrorBuilder("Cannot parse number: Unexpected error")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(0)
                    .build()
            );
        }

        String rawValue = "";
        boolean isInteger = true;
        int offset = 0;
        boolean error = false;

        while(!error && Character.isDigit(seekTo(this.charIndex+offset))){
            rawValue += seekTo(this.charIndex+offset);
            offset++;

            if(seekTo(this.charIndex+offset) == '.') {
                isInteger = false;
                rawValue+='.';
                offset++;

                if(!Character.isDigit(seekTo(this.charIndex+offset)))
                    error = true;
            }
        }

        if(error)
            return Result.failure(new StackableErrorBuilder("Cannot parse number: This is not a number")
                    .withLine(line)
                    .withStatement(currentStatement())
                    .withCursor(offset)
                    .build()
            );


        Token type = null;
        if(isInteger)
            type = new IntegerLiteral(rawValue, line, charIndex);
        else
            type = new FloatLiteral(rawValue, line, charIndex);

        LexerSubParsingResult result = new LexerSubParsingResult(type, offset);

        return Result.success(result);
    }

}
