package io.mkdirs.simpleton.forest_builder.structure;

import io.mkdirs.simpleton.application.Simpleton;
import io.mkdirs.simpleton.evaluator.ASTNode;
import io.mkdirs.simpleton.forest_builder.TreeBuilder;
import io.mkdirs.simpleton.forest_builder.TreeBuilderResult;
import io.mkdirs.simpleton.model.token.Token;
import io.mkdirs.simpleton.result.Result;

import java.util.List;

public abstract class AbstractStructure extends TreeBuilder {

    public AbstractStructure(TreeBuilder head) {
        super(head);
    }

    public AbstractStructure() {
        super();
    }

    public final TreeBuilderResult buildBody(List<Token> tokens){

        ASTNode body = new ASTNode(null);
        ASTNode end = null;

        List<Token> temp = tokens;

        int jmp = 0;//indexOfEOL+1;

        while( end == null && !temp.isEmpty()){


            if(Token.R_BRACKET.equals(temp.get(0))){
                end = new ASTNode(null);
                continue;
            }


            var result = this.head.build(temp);

            if(result.tree().isFailure())
                return result;

            body.addChild(result.tree().get());

            temp = temp.subList(result.jumpIndex(), temp.size());


            jmp += result.jumpIndex();

        }

        if(end == null)
            return new TreeBuilderResult(Result.failure("Unclosed bracket !"), 0);



        if(Simpleton.match(temp, "right_bracket eol"))
            jmp += 2;
        else
            jmp += 1;


        return new TreeBuilderResult(Result.success(body), jmp);

    }
}
