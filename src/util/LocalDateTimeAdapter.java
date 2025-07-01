/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author marcos_miller
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Serializa um objeto LocalDateTime para uma string JSON.
     * @param src O objeto LocalDateTime a ser serializado.
     * @param typeOfSrc O tipo do objeto.
     * @param context Contexto de serialização.
     * @return Um JsonElement (neste caso, um JsonPrimitive contendo a string formatada).
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(FORMATTER));
    }

    /**
     * Desserializa uma string JSON para um objeto LocalDateTime.
     * @param json O JsonElement (neste caso, um JsonPrimitive contendo a string de data/hora).
     * @param typeOfT O tipo do objeto a ser desserializado.
     * @param context Contexto de desserialização.
     * @return O objeto LocalDateTime desserializado.
     * @throws JsonParseException Se a string JSON não puder ser parseada para LocalDateTime.
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), FORMATTER);
    }
}
