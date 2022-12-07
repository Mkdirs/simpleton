package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        if(args.length == 0)
            shell();
    }

    private static void shell(){
        Simpleton simpleton = new Simpleton();
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

            Result<List<Token>> result = simpleton.buildTokens(line);
            if(result.isFailure())
                System.err.println(result.getMessage());
            else {
                Result<List<ASTNode>> trees = simpleton.buildTrees(result.get());
                if(trees.isFailure()){
                    System.err.println(trees.getMessage());
                    continue;
                }
                simpleton.execute(trees.get());
            }
            System.out.println();
        }while(true);
    }
}
