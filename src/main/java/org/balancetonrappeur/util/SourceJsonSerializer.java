package org.balancetonrappeur.util;

import org.balancetonrappeur.entity.Source;
import org.balancetonrappeur.entity.SourceType;

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

    /** Reconstruit le JSON depuis les paramètres bruts du formulaire (pour repopuler après erreur). */
    public static String toJsonFromParams(List<SourceType> types, List<String> titles, List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return "[{\"type\":\"PRESS\",\"title\":\"\",\"url\":\"\"}]";
        }
        var json = new StringBuilder("[");
        for (int i = 0; i < urls.size(); i++) {
            if (i > 0) json.append(",");
            String type  = (types  != null && i < types.size()  && types.get(i)  != null) ? types.get(i).name() : "PRESS";
            String title = (titles != null && i < titles.size() && titles.get(i) != null) ? titles.get(i) : "";
            String url   = urls.get(i) != null ? urls.get(i) : "";
            json.append("{\"type\":\"").append(type).append("\"")
                .append(",\"title\":\"").append(escape(title)).append("\"")
                .append(",\"url\":\"").append(escape(url)).append("\"}");
        }
        return json.append("]").toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}

