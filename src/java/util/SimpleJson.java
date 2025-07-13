package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Map;
import model.User;

public class SimpleJson {
    private static final Gson gson = new Gson();


    public static String toJson(Map<String, Object> map) {
        return gson.toJson(map);
        
    }

    public static String usersToJson(List<User> list) {
        
        return gson.toJson(list);
    }

    public static String userToJson(User u) {
        return gson.toJson(u);
    }

    public static String listToJson(List<Map<String, Object>> list) {
        return gson.toJson(list);
    }

    public static String getString(String json, String key) {
        if (json == null || key == null) return null;
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsString();
            }
        } catch (Exception e) {
            // ignore parse errors
        }
        return null;
    }
    
}