package ru.logonik.unrealminecraft.savers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.arenasmodels.AbstractGameSpot;
import ru.logonik.unrealminecraft.arenasmodels.SpawnPointAbstract;
import ru.logonik.unrealminecraft.models.GameArena;
import ru.logonik.unrealminecraft.models.GameCore;
import ru.logonik.unrealminecraft.savers.sirealizers.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Json implements ISaver {
    private final Plugin plugin;
    private final File folder;
    private final Gson gson;

    public Json(Plugin plugin, File folder) {
        this.plugin = plugin;
        this.folder = folder;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(GameArena.class, new GameArenaSerializer());
        gsonBuilder.registerTypeAdapter(AbstractGameSpot.class, new GameSpotSerializer(plugin));
        gsonBuilder.registerTypeAdapter(Location.class, new LocationSerializer(plugin));
        gsonBuilder.registerTypeAdapter(SpawnPointAbstract.class, new SpawnPointSerializer(plugin));
        //gsonBuilder.registerTypeAdapter(new TypeToken<ItemStack>() {}.getType(), new ItemStackSerializer());
        gsonBuilder.registerTypeAdapter(new TypeToken<List<ItemStack>>() {}.getType(), new ItemStacksSerializer());
        this.gson = gsonBuilder.create();
    }

    @Override
    public GameCore load(Plugin plugin) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(
                    new FileReader(new File(folder, "GameCore.json").toPath().toAbsolutePath().toString()));
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().info("GameCore.json not found");
            return null;
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
        JsonObject object = gson.fromJson(bufferedReader, JsonObject.class);
        if (object == null) {
            return null;
        }
        HashMap<String, GameArena> arenas = gson.fromJson(object.get("arenas"), new TypeToken<HashMap<String, GameArena>>() {
        }.getType());
        Location lobby = gson.fromJson(object.get("lobby"), Location.class);
        return GameCore.getBuilder(plugin).setArenas(arenas).setLobby(lobby).build();
    }

    @Override
    public void save(GameCore gameCore) {
        BufferedWriter bufferedWriter;
        try {
            File jsonFile = new File(folder, "GameCore.json");
            //noinspection ResultOfMethodCallIgnored
            jsonFile.createNewFile();
            JsonObject object = new JsonObject();
            object.add("location", gson.toJsonTree(gameCore.getLobby()));
            object.add("arenas", gson.toJsonTree(gameCore.getArenas()));
            String json = gson.toJson(object);
            bufferedWriter = new BufferedWriter(
                    new FileWriter(jsonFile.toPath().toAbsolutePath().toString(), false));
            bufferedWriter.write(json);
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            //should never happen
            plugin.getLogger().severe("GameCore.json not found");
            e.printStackTrace();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }
}
