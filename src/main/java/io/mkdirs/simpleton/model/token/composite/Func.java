package io.mkdirs.simpleton.model.token.composite;

import io.mkdirs.simpleton.model.token.Token;

import java.util.ArrayList;
import java.util.List;

public class Func extends Token {

    private List<Token> body = new ArrayList<>();
    private Token[] args;
    //private int fullBodyLength = 0;

    public Func(String name){
        super("FUNC", name);
    }

    public Func(){
        this(null);
    }

    public void computeArgs(){

    }

    public List<Token> getBody() {
        return body;
    }

    public void addInBody(Token t){
        body.add(t);
        /*if(this.equals(t)){
            fullBodyLength += (((Func) t).body.size()+1);
        }else
            fullBodyLength++;

         */
    }

    public int getFullBodyLength() {
        int s = 0;
        for(Token t : body){
            if(this.equals(t))
                s += ((Func) t).getFullBodyLength();
            else
                s++;
        }
        return s;
    }

    public void setBody(List<Token> body) {
        this.body = body;
    }

    @Override
    public boolean isKeyword() {
        return false;
    }

    @Override
    public String toString() {
        String bodyStr = String.join(",", body.stream().map(Token::toString).toList());
        return "Token."+name+"('"+literal+"{"+bodyStr+"}')";
    }
}
