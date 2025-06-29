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
import java.lang.reflect.Type;
import java.util.Optional;

/**
 *
 * @author marcos_miller
 */
public class OptionalAdapter<T> implements JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

    /**
     * Serializa um Optional para JSON.
     * Optional.empty() vira null. Optional.of(value) vira 'value'.
     */
    @Override
    public JsonElement serialize(Optional<T> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || !src.isPresent()) { // src == null é para Optional<T> nulo, !src.isPresent() para Optional.empty()
            return null;
        }
        // Usa o contexto para serializar o valor interno, garantindo que tipos complexos sejam tratados
        return context.serialize(src.get());
    }

    /**
     * Desserializa de JSON para Optional.
     * null ou ausência vira Optional.empty(). Outro valor vira Optional.of(value).
     */
    @Override
    public Optional<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return Optional.empty();
        }
        // Usa o contexto para desserializar para o tipo interno (T)
        // typeOfT pode ser Optional<Integer>, precisamos do Integer interno
        Type actualTypeArgument = ((java.lang.reflect.ParameterizedType) typeOfT).getActualTypeArguments()[0];
        return Optional.ofNullable(context.deserialize(json, actualTypeArgument));
    }
}