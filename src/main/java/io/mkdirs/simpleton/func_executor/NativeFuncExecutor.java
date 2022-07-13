package io.mkdirs.simpleton.func_executor;

import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.model.token.literal.StringLiteral;
import io.mkdirs.simpleton.result.Result;

import java.util.Scanner;

public class NativeFuncExecutor implements IFuncExecutor{

    @Override
    public Result<Token> execute(Func func) {
        switch (func.getLiteral()){
            case "print":
                System.out.println(func.getArgs().get(0).getLiteral());
                return Result.success(Token.VOID_KW);

            case "input":
                System.out.print(func.getArgs().get(0).getLiteral());
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                return Result.success(new StringLiteral(input));

            default:
                return Result.failure("Unexpected error");
        }

    }
}
