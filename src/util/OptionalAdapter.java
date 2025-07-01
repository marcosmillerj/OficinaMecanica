/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 *
 * @author marcos_miller
 */
public class OptionalAdapter<T> implements JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

    @Override
    public JsonElement serialize(Optional<T> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || !src.isPresent()) {
            return null;
        }
        return context.serialize(src.get());
    }

    @Override
    public Optional<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return Optional.empty(); 
        }

        Type actualTypeArgument;
        if (typeOfT instanceof ParameterizedType) {
            actualTypeArgument = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        } else {
             return Optional.ofNullable(context.deserialize(json, (Type) ((ParameterizedType) typeOfT).getActualTypeArguments()[0]));
        }
        return null;
    }
}