package ru.logonik.unrealminecraft.savers.sirealizers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ru.logonik.unrealminecraft.util.Util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemStacksSerializer implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>> {
    @Override
    public JsonElement serialize(List<ItemStack> src, Type typeOfSrc, JsonSerializationContext context) {
        ItemStack[] itemStacks = src.toArray(src.toArray(new ItemStack[0]));
        String s = Util.itemStackArrayToBase64(itemStacks);
        return new JsonPrimitive(s);
    }

    @Override
    public List<ItemStack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String string = json.getAsString();
        try {
            ItemStack[] itemStacks = Util.itemStackArrayFromBase64(string);
            return new ArrayList<>(Arrays.asList(itemStacks));
        } catch (IOException e) {
            throw new JsonParseException(e);
        }
    }
}
