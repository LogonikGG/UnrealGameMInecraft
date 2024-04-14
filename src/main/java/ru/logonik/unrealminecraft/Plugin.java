package ru.logonik.unrealminecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.logonik.unrealminecraft.commands.CommandUnrealMinecraft;
import ru.logonik.unrealminecraft.models.GameCore;
import ru.logonik.unrealminecraft.models.Result;
import ru.logonik.unrealminecraft.savers.ISaver;
import ru.logonik.unrealminecraft.savers.Json;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public final class Plugin extends JavaPlugin {

    private GameCore gameCore;
    private FileConfiguration language;

    private final String CANT_FIND = "Error, can't find whats to reply, check the logs";
    private ISaver saver;

    @Override
    public void onEnable() {
        try {
            startPlugin();
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Error while starting");
            getPluginLoader().disablePlugin(this);
        }
    }

    private void startPlugin() throws IOException, InvalidConfigurationException {
        saver = new Json(this, getDataFolder());
        gameCore = saver.load(this);
        if (gameCore == null) {
            gameCore = new GameCore(this);
        }
        saveDefaultConfig();
        saveResource("messages.yml", false);
        language = new YamlConfiguration();
        language.load(new File(getDataFolder(), "messages.yml"));
// new code
        new CommandUnrealMinecraft(this);
    }

    @Override
    public void onDisable() {
        if (gameCore != null) {
            saver.save(gameCore);
        }
    }

    public GameCore getGameCore() {
        return this.gameCore;
    }

    public String getLocalizedMessage(Result result) {
        return getMessage(result.getCode().getValue(), result.getExternalData());
    }

    public String getMessage(String path, Map<String, String> replacements) {
        String res = language.getString(path, CANT_FIND);
        if (res == CANT_FIND) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot find string in message file. " + language.getName() + ". by this path " + path);
            return ChatColor.RED + res;
        }
        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                res = res.replace(entry.getKey(), entry.getValue());
            }
        }
        res = ChatColor.translateAlternateColorCodes('&', res);
        return res;
    }

}
