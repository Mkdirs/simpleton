package io.mkdirs.simpleton.evaluator;

import io.mkdirs.simpleton.evaluator.operator.Operand;
import io.mkdirs.simpleton.evaluator.operator.Operator;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.result.ResultProvider;
import io.mkdirs.simpleton.scope.ScopeContext;

import java.util.*;
import java.util.stream.Stream;

public class ExpressionEvaluator extends ResultProvider {

    public ExpressionEvaluator(ScopeContext scopeContext){
        super(scopeContext);
    }


    public Result<Token> evaluate(ASTNode tree){
        if(tree == null)
            return Result.success(null);

        if(tree.isLeaf())
            return Result.success(tree.getToken());


        Token left = evaluate(tree.getLeft()).get();
        if(Token.VARIABLE_NAME.equals(left)) {
            Token var = this.scopeContext.getVariable(left.getLiteral()).orElse(null);

            if(var == null)
                return pushError("Variable '"+left.getLiteral()+"' does not exist");
            else
                left = var;
        }



        Token right = evaluate(tree.getRight()).get();
        if(Token.VARIABLE_NAME.equals(right)) {
            Token var = this.scopeContext.getVariable(right.getLiteral()).orElse(null);

            if(var == null)
                return pushError("Variable '"+right.getLiteral()+"' does not exist");
            else
                right = var;
        }



        Optional<Operator> operator = getOperator(tree.getToken());

        if(operator.isPresent()){
            Optional<Token> r = operator.get().evaluate(left, right);
            if(r.isEmpty())
                return pushError("Unable to apply '"+operator.get().getToken().getLiteral()+"' on '"+left+"' and '"+right+"'");

            return Result.success(r.get());
        }

        return pushError("Unknown operator: \""+tree.getToken().getLiteral()+"\"");
    }

    private Optional<Operator> getOperator(Token token){
        return Arrays.stream(Operator.OPERATORS).filter(e  -> e.getToken().equals(token)).findFirst();
    }

    public Result<ASTNode> buildTree(List<Token> tokens){

        if(tokens.isEmpty())
            return Result.success(null);
        else if(tokens.size() == 1)
            return Result.success(new ASTNode(null, tokens.get(0), null));

        int closingParenthesis = getClosingParenthesis(0, tokens);

        if(closingParenthesis != -1 && closingParenthesis == tokens.size()-1)
            return buildTree(tokens.subList(1, closingParenthesis));

        int mainOperatorIndex = findMainOperatorIndex(tokens);

        if(mainOperatorIndex == -1)
            return pushError("Unexpected error");


        Token operator = tokens.get(mainOperatorIndex);
        Result<ASTNode> left = buildTree(tokens.subList(0, mainOperatorIndex));
        Result<ASTNode> right = buildTree(tokens.subList(mainOperatorIndex+1, tokens.size()));

        ASTNode tree = new ASTNode(left.get(), operator, right.get());

        return Result.success(tree);
    }

    private int getClosingParenthesis(int start, List<Token> tokens){
        if(!Token.LEFT_PARENTHESIS.equals(tokens.get(start)))
            return -1;

        int openParenthesis = 1;
        int i = start+1;
        while(i < tokens.size() && openParenthesis > 0){
            Token t = tokens.get(i);

            if(Token.LEFT_PARENTHESIS.equals(t))
                openParenthesis++;
            else if(Token.RIGHT_PARENTHESIS.equals(t))
                openParenthesis--;

            i++;
        }

        if(openParenthesis > 0)
            return -1;

        return i-1;
    }

    private int findMainOperatorIndex(List<Token> tokens){
        int mainOperatorIndex = -1;
        int currentOperatorParenthesisScope = 1;
        int mainOperatorParenthesisScope = 1;
        final int parenthesisFactor = 5;

        for(int i = 0; i < tokens.size(); i++){
            Token token = tokens.get(i);

            if(token.equals(Token.LEFT_PARENTHESIS)) {
                currentOperatorParenthesisScope++;
            }else if(token.equals(Token.RIGHT_PARENTHESIS)){
                currentOperatorParenthesisScope--;

                if(currentOperatorParenthesisScope < 1)
                    currentOperatorParenthesisScope = 1;
            }

            Optional<Operator> operator = Arrays.stream(Operator.OPERATORS).filter(e -> e.getToken().equals(token)).findFirst();
            if(operator.isPresent()){
                if(mainOperatorIndex == -1) {
                    mainOperatorIndex = i;
                    mainOperatorParenthesisScope = currentOperatorParenthesisScope;
                }else{
                    final int main = mainOperatorIndex;
                    Optional<Operator> mainOperatorOpt = Arrays.stream(Operator.OPERATORS).filter(e -> e.getToken().equals(tokens.get(main))).findFirst();

                    if(operator.get().getPriority() + (currentOperatorParenthesisScope*parenthesisFactor) <=  mainOperatorOpt.get().getPriority() + (mainOperatorParenthesisScope*parenthesisFactor)){
                        mainOperatorIndex = i;
                        mainOperatorParenthesisScope = currentOperatorParenthesisScope;
                    }
                }
            }

        }

        return mainOperatorIndex;
    }


}
