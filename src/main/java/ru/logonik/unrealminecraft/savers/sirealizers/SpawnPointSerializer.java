package ru.logonik.unrealminecraft.savers.sirealizers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.arenasmodels.ArmorSpawnPoint;
import ru.logonik.unrealminecraft.arenasmodels.HealSpawnPoint;
import ru.logonik.unrealminecraft.arenasmodels.HorseSpawnPoint;
import ru.logonik.unrealminecraft.arenasmodels.SpawnPointAbstract;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SpawnPointSerializer implements JsonDeserializer<SpawnPointAbstract>, JsonSerializer<SpawnPointAbstract> {

    private final Plugin plugin;

    private final String ARMOR_POINT = "ArmorSpawnPoint";
    private final String HEAL_POINT = "HealSpawnPoint";
    private final String HORSE_POINT = "HorseSpawnPoint";

    public SpawnPointSerializer(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public JsonElement serialize(SpawnPointAbstract src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (src instanceof ArmorSpawnPoint) {
            ArmorSpawnPoint point = (ArmorSpawnPoint) src;
            result.addProperty("type", ARMOR_POINT);
            result.add("items", context.serialize(point.getItemStacks()));
        } else if (src instanceof HealSpawnPoint) {
            HealSpawnPoint point = (HealSpawnPoint) src;
            result.addProperty("type", HEAL_POINT);
        } else if (src instanceof HorseSpawnPoint) {
            HorseSpawnPoint point = (HorseSpawnPoint) src;
            result.addProperty("type", HORSE_POINT);
        } else {
            plugin.getLogger().severe("serializer is not written for " + src.getClass().getName());
            plugin.getLogger().severe("I continue only so as not to lose other data");
            return JsonNull.INSTANCE;
        }
        result.addProperty("intervalSpawn", src.getIntervalSpawn());
        result.add("location", context.serialize(src.getLocation(), Location.class));
        return result;
    }

    @Override
    public SpawnPointAbstract deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String type = object.get("type").getAsString();
        long interval = object.get("intervalSpawn").getAsLong();
        Location location = context.deserialize(object.get("location"), Location.class);
        switch (type) {
            case ARMOR_POINT: {
                Type itemsTypeArray = new TypeToken<ArrayList<ItemStack>>() {}.getType();
                ArrayList<ItemStack> items = context.deserialize(object.get("items"), itemsTypeArray);
                return new ArmorSpawnPoint(location, null, items, interval);
            }
            case HEAL_POINT: {
                return new HealSpawnPoint(location, null, interval);
            }
            case HORSE_POINT: {
                return new HorseSpawnPoint(location, null, interval);
            }
            default: {
                plugin.getLogger().severe("Type of point is undefined '" + type + "'");
                throw new JsonParseException("Type of point is undefined '" + type + "'");
            }
        }
    }
}
