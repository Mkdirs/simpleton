package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.result.Result;
import io.mkdirs.simpleton.scope.ScopeContext;
import io.mkdirs.simpleton.statement.Statement;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        if(args.length == 0)
            shell();
    }

    private static void shell(){
        System.out.println("Enter \":q\" to exit\n");
        Scanner scanner = new Scanner(System.in);
        //ScopeContext ctx = new ScopeContext();
        do{
            System.out.print("> ");
            String line = scanner.nextLine();

            if(line.isBlank())
                continue;

            if(line.equals(":q"))
                break;

            Result<List<Statement>> result = Simpleton.build(line);
            if(result.isFailure())
                System.err.println(result.getMessage());
            else {
                Statement statement = result.get().get(0);
                System.out.println("\t~"+statement.toText());
            }
            System.out.println();
        }while(true);
    }
}
