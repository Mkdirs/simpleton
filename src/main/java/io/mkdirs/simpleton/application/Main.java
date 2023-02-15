package io.mkdirs.simpleton.application;

import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        if(args.length == 0)
            shell();
        else if(args.length == 1)
            runFile(args[0]);
    }

    private static void runFile(String path){
        File f = new File(path);

        if(!f.exists()){
            System.err.println("The file "+f.getAbsolutePath()+" doesn't exist");
            return;
        }

        if(!f.isFile()){
            System.err.println("You must provide a file !");
            return;
        }

        try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
            Simpleton simpleton = new Simpleton();
            String text = "";
            for(String line : reader.lines().toList()){
                text += line+"\n";
            }
            Result<List<Token>> tokensRes = simpleton.buildTokens(text);
            if(tokensRes.isFailure()){
                System.err.println(tokensRes.getMessage());
                return;
            }

            Result<List<ASTNode>> nodesRes = simpleton.buildTrees(tokensRes.get());

            if(nodesRes.isFailure()){
                System.err.println(nodesRes.getMessage());
                return;
            }


            var result = simpleton.execute(nodesRes.get());
            if(result.isFailure())
                System.err.println(result.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

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
