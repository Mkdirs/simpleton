package io.mkdirs.simpleton.model.token;

public class VariableName extends Token {

    public VariableName(String literal){super("VARIABLE_NAME", literal);}
    public VariableName() {
        this(null);
    }


    @Override
    public boolean isKeyword() {
        return false;
    }
}
