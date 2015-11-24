package com.alpha.quiztomizador.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

public class GenericJSON{

   private static Gson gson =  new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .serializeNulls().create();

    private static Class<?> _clazz;

    public static <T> T fromJSON(String strJson, Class clazz)
    {
        return (T) gson.fromJson(strJson, clazz);
    }

    public static <T> String toJSON(T obj)
    {
        return gson.toJson(obj);
    }

    public static <T> String toJSON(T obj, String... exclusao )
    {
        GenericExclusionStrategy ges = new GenericExclusionStrategy(obj.getClass(), exclusao);
        Gson gson =  new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .setExclusionStrategies(new GenericExclusionStrategy[]{ges})
                .serializeNulls().create();
        return gson.toJson(obj);
    }

    public static <T> T fromJSON(String strJson, Class clazz, String... exclusao)
    {
        GenericExclusionStrategy ges = new GenericExclusionStrategy(clazz, exclusao);
        Gson gson =  new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .setExclusionStrategies(new GenericExclusionStrategy[]{ges})
                .serializeNulls().create();
        return (T) gson.fromJson(strJson, clazz);
    }

}

