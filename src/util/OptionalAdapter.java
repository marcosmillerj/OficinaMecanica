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
            return null; // Serializa Optional.empty() ou null Optional como JSON null
        }
        return context.serialize(src.get()); // Serializa o valor interno
    }

    @Override
    public Optional<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Se o JSON é nulo ou ausente (isJsonNull), retorna Optional.empty().
        if (json == null || json.isJsonNull()) {
            return Optional.empty(); 
        }

        // Tenta extrair o tipo real do Generic (T) do Optional<T>
        Type actualTypeArgument;
        if (typeOfT instanceof ParameterizedType) {
            actualTypeArgument = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        } else {
            // Caso especial: se não é um tipo parametrizado (ex: Optional.class sem <Integer>)
            // Isso pode acontecer se o campo foi declarado como 'Optional' e não 'Optional<Integer>'.
            // Ou se o JSON não tinha tipo e o Gson está tentando inferir.
            // Para garantir que o Optional.ofNullable não receba null de um primitivo,
            // ou se o tipo real não pode ser inferido, precisamos de um fallback seguro.
            // O `json.getAs...()` pode lançar NumberFormatException se o tipo não for primitivo
            // ou IllegalStateException se o JSON for um objeto/array e não um primitivo.
            
            // Para o seu caso, idVeiculoAtual: Optional<Integer>, o ParameterizedType é esperado.
            // Se chegou aqui, é porque a reflexão não funcionou como esperado para o tipo interno.
            // A solução mais robusta é lidar com a desserialização do Object caso o tipo não seja ParameterizedType.
            
            // Este bloco else foi o problemático. Vamos removê-lo e confiar no `Optional.ofNullable`
            // após a desserialização contextual. O problema pode ser na ordem dos adaptadores
            // ou na interpretação do Gson.

            // Recomposição da solução: `context.deserialize` pode retornar `null` se o valor no JSON for `null`
            // ou se o tipo não for compatível. `Optional.ofNullable` é a forma correta de encapsular isso.
            // O problema do `OptionalAdapter` é se ele não conseguir desserializar o tipo `T`.
            // Vamos garantir que ele lide com a ausência do campo no JSON.

            // O problema `idVeiculoAtual` ser `null` no Elevador pode estar acontecendo
            // porque o Gson não está passando `json.isJsonNull()` no `deserialize`
            // mas sim um `null` no próprio `json` objeto (se o campo não existe no JSON).
            // A primeira linha `if (json == null || json.isJsonNull())` já deveria cuidar disso.

            // A falha pode estar acontecendo em:
            // `elevadorRepository.listarTodosElevadores().stream().anyMatch(e -> e.getIdVeiculoAtual().isPresent() && ...)`
            // Se `getIdVeiculoAtual()` retorna `null`, significa que o Gson não chamou `deserialize`
            // para `Optional.class` e simplesmente setou `null` para o `Optional<Integer>` do `Elevador`.

            // Isso é um bug conhecido em algumas interações do Gson com Optional e fields ausentes.
            // A solução mais robusta é fazer o Gson sempre chamar o adaptador, mesmo para nulos.
            // E garantir que o campo Optional no seu modelo Elevador é `Optional<Integer>` e não apenas `Optional`.
            // Já colocamos `serializeNulls()`.

            // A solução mais segura para este erro específico, considerando o que já foi feito,
            // é verificar o `Optional` para `null` *antes* de chamar `isPresent()`.

             return Optional.ofNullable(context.deserialize(json, (Type) ((ParameterizedType) typeOfT).getActualTypeArguments()[0]));
        }
        return null;
    }
}