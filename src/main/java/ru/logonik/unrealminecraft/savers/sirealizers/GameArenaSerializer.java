package ru.logonik.unrealminecraft.savers.sirealizers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import ru.logonik.unrealminecraft.arenasmodels.AbstractGameSpot;
import ru.logonik.unrealminecraft.models.GameArena;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GameArenaSerializer implements JsonSerializer<GameArena>, JsonDeserializer<GameArena> {

    @Override
    public JsonElement serialize(GameArena src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("name", src.getName());
        object.add("game_spots", context.serialize(src.getGameSpots(), new TypeToken<Map<String, AbstractGameSpot>>(){}.getType()));
        object.add("arena_lobby", context.serialize(src.getArenaLobby(), Location.class));
        return object;
    }

    @Override
    public GameArena deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        Location arenaLobby = context.deserialize(object.get("arena_lobby"), Location.class);
        Type gameSpotsToken = new TypeToken<HashMap<String, AbstractGameSpot>>() {
        }.getType();
        HashMap<String, AbstractGameSpot> gameSpots = context.deserialize(object.get("game_spots"), gameSpotsToken);
        String name = object.get("name").getAsString();
        GameArena arena = new GameArena(null, arenaLobby, name);
        arena.setSpots(gameSpots);
        return arena;
    }
}
