package com.dobbinsoft.netting.base.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

    private static final Gson gson = new Gson();

    public static <T> T parse(String json, Class<T> clazz) {
        T t = gson.fromJson(json, clazz);
        return t;
    }

    public static <T> T parse(String json, TypeToken<T> typeToken) {
        T t = gson.fromJson(json, typeToken);
        return t;
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

}
