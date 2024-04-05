package ru.logonik.unrealminecraft.savers.sirealizers;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import ru.logonik.unrealminecraft.Plugin;

import java.lang.reflect.Type;

public class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

    private final Plugin plugin;

    public LocationSerializer(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String worldName = jsonObject.get("world").getAsString();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.get("yaw").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
            plugin.getLogger().severe("World with name '" + worldName + "' is not load. Will use default world");
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("world", src.getWorld().getName());
        result.addProperty("x", src.getX());
        result.addProperty("y", src.getY());
        result.addProperty("z", src.getZ());
        result.addProperty("yaw", src.getYaw());
        result.addProperty("pitch", src.getPitch());
        return result;
    }
}
