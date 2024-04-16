package ru.logonik.unrealminecraft.savers.sirealizers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemStacksSerializer implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>> {
    @Override
    public JsonElement serialize(List<ItemStack> src, Type typeOfSrc, JsonSerializationContext context) {
        Type token = new TypeToken<Map<String, Object>>() {}.getType();
        JsonArray array = new JsonArray();
        for (ItemStack itemStack : src) {
            array.add(context.serialize(itemStack.serialize(), token));
        }
        return array;
    }

    @Override
    public List<ItemStack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray asJsonArray = json.getAsJsonArray();
        ArrayList<ItemStack> result = new ArrayList<>();
        Type token = new TypeToken<Map<String, Object>>() {}.getType();
        for (JsonElement jsonElement : asJsonArray) {
            result.add(ItemStack.deserialize(context.deserialize(jsonElement, token)));
        }
        return result;
    }
}
