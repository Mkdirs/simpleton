package io.mkdirs.simpleton.statement;

import java.io.Serializable;
import java.util.HashMap;

public abstract class Statement implements Serializable {

    protected HashMap<String, Object> infos = new HashMap<>();


    public <T> T getInfo(String key){
        if(!infos.containsKey(key))
            return null;
        else
            return (T) infos.get(key);
    }

    public  <T> void setInfo(String key, T value){
        this.infos.put(key, value);
    }


    @Override
    public String toString() {
        return infos.toString();
    }
}
