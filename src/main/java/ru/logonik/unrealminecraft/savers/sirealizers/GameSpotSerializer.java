package ru.logonik.unrealminecraft.savers.sirealizers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.arenasmodels.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameSpotSerializer implements JsonSerializer<AbstractGameSpot>, JsonDeserializer<AbstractGameSpot> {

    private final String MIDDLE_SPOT = "middle_spot";
    private final String BASE_SPOT = "base_spot";

    private final Plugin plugin;

    public GameSpotSerializer(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public JsonElement serialize(AbstractGameSpot src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (src instanceof MiddleSpot) {
            result.addProperty("type", MIDDLE_SPOT);
        } else if (src instanceof BaseSpot) {
            result.addProperty("type", BASE_SPOT);
        } else {
            plugin.getLogger().severe("serializer is not written for " + src.getClass().getName());
            plugin.getLogger().severe("I continue only so as not to lose other data");
            return JsonNull.INSTANCE;
        }
        List<String> connectionNames = src.getConnections().stream().map(AbstractGameSpot::getName).collect(Collectors.toList());
        result.addProperty("name", src.getName());
        result.add("connections", context.serialize(connectionNames, new TypeToken<List<String>>(){}.getType()));
        result.add("location", context.serialize(src.getLocation(), Location.class));
        result.add("items_points", context.serialize(src.getItemsPoints(), new TypeToken<List<SpawnPointAbstract>>(){}.getType()));
        result.add("spawns", context.serialize(src.getSpawns(), new TypeToken<ArrayList<Location>>(){}.getType()));
        return result;
    }

    @Override
    public AbstractGameSpot deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String type = object.get("type").getAsString();
        String name = object.get("name").getAsString();
        ArrayList<SpawnPointAbstract> itemsPoints = context.deserialize(object.get("items_points"), new TypeToken<ArrayList<SpawnPointAbstract>>() {}.getType());
        ArrayList<Location> spawns = context.deserialize(object.get("spawns"), new TypeToken<ArrayList<Location>>() {}.getType());
        Type connectionToken = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> connectionNames = context.deserialize(object.get("connections"), connectionToken);
        Location location = context.deserialize(object.get("location"), Location.class);
        AbstractGameSpot spot;
        switch (type) {
            case MIDDLE_SPOT: {
                spot = new MiddleSpot(location, name);
                break;
            }
            case BASE_SPOT: {
                spot = new BaseSpot(location, name);
                break;
            }
            default: {
                plugin.getLogger().severe("Type '" + type + "' of spot is undefined");
                throw new JsonParseException("Type '" + type + "' of spot is undefined");
            }
        }
        spot.setSpawnLocations(spawns);
        spot.setItemPoints(itemsPoints);
        spot.setSerializationConnectionNames(connectionNames);
        return spot;
    }
}
