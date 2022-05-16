package dev.oxoo2a.sim4da;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Message {
    
    private static final Gson SERIALIZER = new Gson();
    
    private final HashMap<String, String> content;
    
    public Message() {
        content = new HashMap<>();
    }
    
    protected Message(HashMap<String, String> content) {
        this.content = content;
    }
    
    public Message add(String key, String value) {
        content.put(key, value);
        return this;
    }
    
    public Message add(String key, int value) {
        content.put(key, String.valueOf(value));
        return this;
    }
    
    public String query(String key) {
        return content.get(key);
    }
    
    public Map<String, String> getMap() {
        return content;
    }
    
    public String toJson() {
        return serialize(content);
    }
    
    public static Message fromJson(String s) {
        Type contentType = new TypeToken<HashMap<String, String>>() {}.getType();
        return new Message(SERIALIZER.fromJson(s, contentType));
    }
    
    private static synchronized String serialize(Map<String, String> content) {
        return SERIALIZER.toJson(content); // Not sure about thread safety of Gson
    }
}
