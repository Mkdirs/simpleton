package io.mkdirs.simpleton.func_executor;

import io.mkdirs.simpleton.model.Type;
import io.mkdirs.simpleton.model.Value;
import io.mkdirs.simpleton.model.error.StackableError;
import io.mkdirs.simpleton.model.error.StackableErrorBuilder;
import io.mkdirs.simpleton.model.token.composite.Func;
import io.mkdirs.simpleton.result.Result;

import java.util.Scanner;

public class NativeFuncExecutor implements IFuncExecutor{

    @Override
    public Result<Value, StackableError> execute(Func func) {
        switch (func.name){
            case "print":
                System.out.println(func.getArgs().get(0).value());
                return Result.success(Value.VOID);

            case "input":
                System.out.print(func.getArgs().get(0).value());
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                return Result.success(new Value(Type.STRING, input));

            default:
                return Result.failure(new StackableErrorBuilder("Unexpected error")
                        .withStatement("")
                        .build()
                );
        }

    }
}
