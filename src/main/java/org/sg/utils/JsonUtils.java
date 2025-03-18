package org.sg.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;

public class JsonUtils {
    static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper;
    }
    public static <T> String serialize(T input) {
        try {
            ObjectMapper mapper = getMapper();
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, input);
            return writer.toString();
        } catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static <T> T deserialize(final String json, final TypeReference<T> type) {
        try {
            return getMapper().readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T deserialize(final String json, final Class<T> type) {
        try {
            return getMapper().readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }
    public static JsonNode parse(String s) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(s);
        } catch(Exception ex) {
            return null;
        }
    }

    public static String getStringOrEmpty(JsonNode node, String property) {
        JsonNode tmp = node.get(property);
        return tmp == null ? "" : tmp.asText();
    }

    public static int getIntOrEmpty(JsonNode node, String property) {
        JsonNode tmp = node.get(property);
        return tmp == null ? 0 : tmp.asInt();
    }
}
