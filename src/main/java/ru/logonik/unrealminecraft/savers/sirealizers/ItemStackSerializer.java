package ru.logonik.unrealminecraft.savers.sirealizers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        Type token = new TypeToken<Map<String, Object>>() {}.getType();
        return context.serialize(src.serialize(), token);
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Type token = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> map = context.deserialize(json, token);
        return ItemStack.deserialize(map);
    }
}
