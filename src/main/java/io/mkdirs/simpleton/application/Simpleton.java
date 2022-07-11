package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.evaluator.ExpressionEvaluator;
import io.mkdirs.simpleton.lexer.Lexer;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.ScopeContext;
import io.mkdirs.simpleton.scope.VariableHolder;
import io.mkdirs.simpleton.statement.*;

import java.util.*;

public class Simpleton {

    private static ScopeContext scope = new ScopeContext();
    private static final ExpressionEvaluator evaluator = new ExpressionEvaluator(scope);


    public static void build(String text){
        String[] lines = text.split("\n");
        int lineIndex = 0;

        Lexer lexer = new Lexer(scope);
        List<Statement> statements = new ArrayList<>();

        while(lineIndex < lines.length){
            String line = lines[lineIndex];
            if(line.isBlank()){
                lineIndex++;
                continue;
            }

            scope.setLine(line);
            Result<List<Token>> tokensResult = lexer.parse();
            if(tokensResult.isFailure()){
                System.err.println(tokensResult.getMessage());
                break;
            }

            System.out.println(lines[lineIndex]);
            List<Token> tokens = tokensResult.get();
            Result<Statement> result = checkTokens(tokens, lineIndex);

            if(result.isFailure()){
                System.err.println(result.getMessage());
                break;
            }

            Statement statement = result.get();




            statements.add(statement);


            System.out.println(result.get());

            lineIndex++;
        }

        System.out.println("Build complete\n");

        for (Statement s : statements){
            System.out.println(s.getClass().getSimpleName());
        }
    }

    private static Result<Statement> checkTokens(List<Token> tokens, int lineIndex){
        if(Token.LET_KW.equals(tokens.get(0))){
            return checkVariableDeclaration(tokens, lineIndex);

        }else if(Token.VARIABLE_NAME.equals(tokens.get(0))){
            return checkVariableAssignation(tokens, lineIndex);

        }else if(Token.IF_KW.equals(tokens.get(0))) {
            return checkIfClosure(tokens, lineIndex);

        }

        return error("Unexpected error*", lineIndex);
    }

    private static Result<Statement> checkVariableDeclaration(List<Token> tokens, int lineIndex){
        /*
                let name:type
                let name:type = expr
                let name = expr
                 */

        if(tokens.size() < 4){
            return error("Syntax error", lineIndex);
        }

        if(!Token.VARIABLE_NAME.equals(tokens.get(1))){
            return error("Unexpected token", lineIndex);
        }

        Token name = tokens.get(1);
        if(scope.getVariable(name.getLiteral()).isPresent()){
            return error("Variable '"+name.getLiteral()+"' already exists", lineIndex);
        }

        if(Token.COLON.equals(tokens.get(2))){
            //let name:type
            //let name:type = expr
            HashMap<Token, Token> types = new HashMap<>();
            types.putAll(Map.ofEntries(
                    Map.entry(Token.INT_KW, Token.INTEGER_LITERAL),
                    Map.entry(Token.FLOAT_KW, Token.FLOAT_LITERAL),
                    Map.entry(Token.CHAR_KW, Token.CHARACTER_LITERAL),
                    Map.entry(Token.STRING_KW, Token.STRING_LITERAL),
                    Map.entry(Token.BOOL_KW, Token.BOOLEAN_LITERAL)

            ));

            Token expectedType = tokens.get(3);

            if(!types.containsKey(expectedType)){
                return error("Unknown type '"+expectedType.getLiteral()+"'", lineIndex);
            }

            if(tokens.size() == 4){
                //let name:type
                scope.pushVariable(name.getLiteral(), expectedType);

                return Result.success(
                        new VariableDeclaration()
                                .name(name.getLiteral())
                                .type(expectedType)
                );

            }else{
                if(!Token.EQUALS.equals(tokens.get(4))){
                    return error("Expected token '=' but instead got '"+tokens.get(4).getLiteral()+"'", lineIndex);
                }

                //let name:type = expr

                Result<HashMap<String, Object>> res = assignVariable(name.getLiteral(), tokens.subList(5, tokens.size()), expectedType);

                if(res.isFailure())
                    return error(res.getMessage(), lineIndex);


                return Result.success(
                        new VariableDeclaration()
                                .name(name.getLiteral())
                                .type((Token) res.get().get("type"))
                                .value((String) res.get().get("value"))
                );
            }

        }else{
            //let name = expr
            if(!Token.EQUALS.equals(tokens.get(2))){
                return error("Expected token '=' but instead got '"+tokens.get(2).getLiteral()+"'", lineIndex);
            }

            Result<HashMap<String, Object>> res = assignVariable(name.getLiteral(), tokens.subList(3, tokens.size()));
            if(res.isFailure())
                return error(res.getMessage(), lineIndex);



            return Result.success(
                    new VariableDeclaration()
                            .name(name.getLiteral())
                            .type( (Token) res.get().get("type"))
                            .value((String) res.get().get("value"))
            );
        }
    }

    private static Result<Statement> checkVariableAssignation(List<Token> tokens, int lineIndex){
        //name = expr

        Token name = tokens.get(0);

        if(scope.getVariable(name.getLiteral()).isEmpty()){
            return error("Variable '"+name.getLiteral()+"' does not exist", lineIndex);
        }

        if(tokens.size() <3){
            return error("Syntax error", lineIndex);
        }

        if(!Token.EQUALS.equals(tokens.get(1))){
            return error("Expected token '=' but instead got '"+tokens.get(1).getLiteral()+"'", lineIndex);
        }

        Token expectedType = scope.getVariable(name.getLiteral()).get().getType();

        Result<HashMap<String, Object>> res = assignVariable(name.getLiteral(), tokens.subList(2, tokens.size()), expectedType);
        if(res.isFailure())
            return error(res.getMessage(), lineIndex);

        return Result.success(
                new VariableAssignation()
                        .name(name.getLiteral())
                        .value( (String) res.get().get("value"))
        );
    }

    private static Result<Statement> checkIfClosure(List<Token> tokens, int lineIndex){
        //if expr then {

        if(tokens.size() < 4)
            return error("Syntax error", lineIndex);

        final int expectedLeftBracketIndex = tokens.size()-1;
        final int expectedThenIndex = expectedLeftBracketIndex-1;

        Result r = evaluate(tokens.subList(1, expectedThenIndex));
        if(r.isFailure())
            return error(r.getMessage(), lineIndex);

        Token value = (Token)r.get();

        if(Token.VARIABLE_NAME.equals(value)){
            if(scope.getVariable(value.getLiteral()).isEmpty())
                return error("Variable '"+value.getLiteral()+"' does not exist", lineIndex);

            value = scope.getVariable(value.getLiteral()).get().getValue();
        }

        if(!Token.BOOLEAN_LITERAL.equals(value))
            return error("Expected "+Token.BOOLEAN_LITERAL+" but instead got "+value, lineIndex);

        if(!Token.THEN_KW.equals(tokens.get(expectedThenIndex)))
            return error("Expected '"+Token.THEN_KW+"' but instead got '"+tokens.get(expectedThenIndex)+"'", lineIndex);

        if(!Token.L_BRACKET.equals(tokens.get(expectedLeftBracketIndex)))
            return error("Expected '"+Token.L_BRACKET+"' but instead got '"+tokens.get(expectedLeftBracketIndex)+"'", lineIndex);



        return null;

        /*return Result.success(
                new IfClosure()
                        .value(value.getLiteral())
                        .line(lineIndex+1)
        );

         */


    }

    private static Result evaluate(List<Token> tokens){
        Result<ASTNode> treeResult = evaluator.buildTree(tokens);

        if(treeResult.isFailure())
            return treeResult;

        Result<Token> result = evaluator.evaluate(treeResult.get());

        return result;
    }

    private static Result error(String message, int lineIndex){
        return Result.failure("Error at line "+(lineIndex+1)+":\n"+scope.getLine()+"\n"+message);
    }

    /*public static void run(String text){
        String[] lines = text.split("\n");

        Lexer lexer = new Lexer(scope);

        for(String line : lines){
            if(line.isBlank())
                continue;


            System.out.println("Line: "+line);
            scope.setLine(line);


            if(switchedScope) {
                lexer.setScopeContext(scope);
                evaluator.setScopeContext(scope);
                switchedScope = false;
            }

            Result<List<Token>> lexerResult = lexer.parse();

            if(lexerResult.isFailure()){
                System.out.println(lexerResult.getMessage());
                break;
            }

            List<Token> tokens = lexerResult.get();

            if(Token.RIGHT_BRACKET.equals(tokens.get(0))){

                scope = scope.getParent() == null ? scope : scope.getParent();

                System.out.println("skipperScopeId = "+skipperScopeId);
                System.out.println("scope: "+scope.getId());
                if(!scope.isExpectingStructureClosure()){
                    System.out.println(line);
                    System.out.println("Unexpected '"+Token.RIGHT_BRACKET+"'");
                    return;
                }


                //boolean skip = skipperScopeId != scope.getId();
                if(skipperScopeId == scope.getId()) {
                    skipperScopeId = -1;
                    System.out.println("ccc");
                    //skipStructure = false;

                }else if(skipStructure && skipperScopeId == -1){
                    System.out.println("dd");
                    skipStructure = false;
                    scope.setExpectStructureClosure(false);
                }

                //skipStructure = skip;
                System.out.println("skip : "+skipStructure);
                //scope.setSkipIf(skip);

                lexer.setScopeContext(scope);
                evaluator.setScopeContext(scope);

            }




            System.out.println(String.join(",", tokens.stream().map(Objects::toString).toList()));

            checkForVariableDeclaration(tokens);
            checkForVariableAssignation(tokens);
            checkForStructures(tokens);

            System.out.println();

        }
    }

     */



    private static Result<HashMap<String, Object>> assignVariable(String name, List<Token> tokens, Token expectedType){

        Result result = evaluate(tokens);
        if(result.isFailure())
            return Result.failure(result.getMessage());


        Token value = (Token)result.get();
        Token type = expectedType;
        if(Token.VARIABLE_NAME.equals(value)){
            Optional<VariableHolder> opt = scope.getVariable(value.getLiteral());
            if(opt.isEmpty()){
                return Result.failure("Variable '"+value.getLiteral()+"' does not exist");
            }

            value = opt.get().getValue();
            type = opt.get().getType();
        }

        HashMap<String, Object> map = new HashMap();
        if(type == null) {
            scope.pushVariable(name, scope.typeOf(value), value);

            map.put("type", scope.typeOf(value));
            map.put("value", value.getLiteral());
        }else if(Token.NULL_KW.equals(type) && !Token.NULL_KW.equals(value)){
            return Result.failure("Type of '"+name+"' is unknown");

        }else if(!type.equals(scope.typeOf(value)) && !Token.NULL_KW.equals(value)) {
            return Result.failure("Expected '"+type+"' but instead got '"+scope.typeOf(value)+"'");
        }else{
            scope.pushVariable(name, type, value);

            map.put("type", type);
            map.put("value", value.getLiteral());
        }

        return Result.success(map);
    }

    private static Result assignVariable(String name, List<Token> tokens){return assignVariable(name, tokens, null);}


    /*private static void checkForIf(List<Token> tokens){

        if(tokens.size() < 2 || !Token.LEFT_PARENTHESIS.equals(tokens.get(1))){
            System.out.println("Expected '"+Token.LEFT_PARENTHESIS+"'");
            return;
        }

        if(!Token.LEFT_BRACKET.equals(tokens.get(tokens.size()-1))){
            System.out.println("Expected '"+Token.LEFT_BRACKET+"'");
            return;
        }

        int thenPos = tokens.size()-2;

        if(!Token.THEN_KW.equals(tokens.get(thenPos))){
            System.out.println("Expected '"+Token.THEN_KW+"'");
            return;
        }


        int finalClosingParenthesis = thenPos-1;
        if(!Token.RIGHT_PARENTHESIS.equals(tokens.get(finalClosingParenthesis))){
            System.out.println("Expected '"+Token.RIGHT_PARENTHESIS+"'");
            return;
        }

        List<Token> expr = tokens.subList(2, finalClosingParenthesis);
        if(expr.isEmpty()){
            System.out.println("Expected a boolean expression");
            return;
        }
        Result<ASTNode> tree = evaluator.buildTree(expr);

        if(tree.isFailure()){
            System.out.println(tree.getMessage());
            return;
        }

        Result<Token> result = evaluator.evaluate(tree.get());

        if(result.isFailure()){
            System.out.println(result.getMessage());
            return;
        }

        Token valueToken = null;

        if(Token.VARIABLE_NAME.equals(result.get())){
            Optional<Token> opt = scope.getVariable(result.get().getLiteral());
            if(opt.isEmpty()){
                System.out.println("Variable '"+result.get().getLiteral()+"' does not exist");
                return;
            }

            valueToken = opt.get();
        }else
            valueToken = result.get();

        if(!Token.BOOLEAN_LITERAL.equals(valueToken)){
            System.out.println("Expected boolean expression");
            return;
        }

        boolean value = Boolean.parseBoolean(valueToken.getLiteral());

        //scope.setSkipIf(!value || scope.isSkippingIf());
        scope.setExpectStructureClosure(true);
        System.out.println("Scope before = "+scope.getId());

        if(!value && !skipStructure) {
            skipperScopeId = scope.getId();
            skipStructure = true;
        }

        scope = scope.child();
        System.out.println("Scope after = "+scope.getId());

        //scope.setSkipIf(scope.getParent().isSkippingIf());
        switchedScope = true;

    }

    private static void checkForStructures(List<Token> tokens){

        Token first = tokens.get(0);

        if(Token.IF_KW.equals(first)){
            checkForIf(tokens);
        }
    }

     */

}
