package util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import model.User;

public class SimpleJson {
    public static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Iterator<Map.Entry<String,Object>> it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String,Object> e = it.next();
            sb.append('"').append(escape(e.getKey())).append('"').append(':');
            Object v = e.getValue();
            if (v == null) {
                sb.append("null");
            } else if (v instanceof Number || v instanceof Boolean) {
                sb.append(v.toString());
            } else {
                sb.append('"').append(escape(v.toString())).append('"');
            }
            if(it.hasNext()) sb.append(',');
        }
        sb.append('}');
        return sb.toString();
    }

    public static String usersToJson(List<User> list) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=0;i<list.size();i++) {
            User u = list.get(i);
            sb.append(userToJson(u));
            if (i < list.size()-1) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    public static String userToJson(User u) {
        return "{" +
            "\"id\":" + u.getId() +
            ",\"email\":\"" + escape(u.getEmail()) + "\"" +
            ",\"pointBalance\":" + u.getPointBalance() +
            ",\"locked\":" + u.isLocked() +
            "}";
    }

    public static String getString(String json, String key) {
        String pattern = "\"" + key.replace("\"", "\\\"") + "\"\s*:\s*\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) return null;
        idx += pattern.length();
        int end = json.indexOf('"', idx);
        if (end == -1) return null;
        return json.substring(idx, end);
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}