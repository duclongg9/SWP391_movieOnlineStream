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
            ",\"username\":\"" + escape(u.getUsername()==null?"":u.getUsername()) + "\"" +
            ",\"fullName\":\"" + escape(u.getFullName()==null?"":u.getFullName()) + "\"" +
            ",\"phone\":\"" + escape(u.getPhone()==null?"":u.getPhone()) + "\"" +
            ",\"email\":\"" + escape(u.getEmail()) + "\"" +
            ",\"pointBalance\":" + u.getPointBalance() +
            ",\"locked\":" + u.isLocked() +
            ",\"deleted\":" + u.isDeleted() +
            "}";
    }

    public static String listToJson(List<Map<String,Object>> list) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=0;i<list.size();i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size()-1) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    public static String getString(String json, String key) {
        if (json == null || key == null) return null;
        String pattern = "\"" + java.util.regex.Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}