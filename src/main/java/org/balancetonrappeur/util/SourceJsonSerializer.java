package org.balancetonrappeur.util;

import org.balancetonrappeur.entity.Source;

import java.util.List;

public final class SourceJsonSerializer {

    private SourceJsonSerializer() {}

    public static String toJson(List<Source> sources) {
        var json = new StringBuilder("[");
        int i = 0;
        for (var s : sources) {
            if (i++ > 0) json.append(",");
            json.append("{\"type\":\"").append(s.getType().name()).append("\"")
                .append(",\"title\":\"").append(escape(s.getTitle())).append("\"")
                .append(",\"url\":\"").append(escape(s.getUrl())).append("\"}");
        }
        return json.append("]").toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}

