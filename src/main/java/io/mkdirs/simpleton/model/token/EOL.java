package io.mkdirs.simpleton.model.token;

public class EOL extends Token{

    public EOL(){
        super("EOL");
    }

    @Override
    public boolean isKeyword() {
        return false;
    }
}
