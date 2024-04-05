package ru.logonik.unrealminecraft.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.arenasmodels.AbstractGameSpot;
import ru.logonik.unrealminecraft.arenasmodels.SpawnPointAbstract;

import java.util.*;

public class GameCore implements Listener {

    private HashMap<String, GameArena> arenas;
    private final HashMap<UUID, Gamer> gamers;
    private final HashMap<Gamer, GameArena> associateTableGamers;
    private Location lobby;
    private final Plugin plugin;


    public GameCore(Plugin plugin) {
        this.plugin = plugin;
        arenas = new HashMap<>();
        gamers = new HashMap<>();
        associateTableGamers = new HashMap<>();
    }

    public Result createArena(Location lobbyLocation,String name) {
        if (name == null || name.isEmpty()) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        if (arenas.get(name) != null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        if (lobbyLocation == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);

        }
        GameArena gameArena = new GameArena(this, lobbyLocation, name);
        arenas.put(name, gameArena);
        return new Result(true, LangCode.SUCCESS);
    }

    public Result removeArena(String name) {
        GameArena gameArena = arenas.get(name);
        if (gameArena == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        arenas.remove(name);
        return new Result(true, LangCode.SUCCESS);
    }

    public Result joinArena(String arena, Player player) {
        GameArena gameArena = arenas.get(arena);
        if (gameArena == null || player == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        return gameArena.joinArena(getGamer(player));
    }

    public Result joinArena(GameArena arena, Player player) {
        return arena.joinArena(getGamer(player));
    }

    public Gamer getGamer(Player player) {
        Objects.requireNonNull(player);
        Gamer gamer = gamers.get(player.getUniqueId());
        if (gamer == null) {
            gamer = new Gamer(player);
            gamers.put(player.getUniqueId(), gamer);
        }
        return gamer;
    }

    public Gamer tryGetGamer(Player player) {
        return gamers.get(player.getUniqueId());
    }

    public ArrayList<String> getArenasNames() {
        return new ArrayList<>(arenas.keySet());
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public GameArena getArena(String name) {
        return arenas.get(name);
    }

    public GameArena getArena(Player player) {
        Gamer gamer = gamers.get(player.getUniqueId());
        if (gamer == null) {
            return null;
        }
        return associateTableGamers.get(gamer);
    }

    public Result joinTeam(Player player, String name) {
        final Gamer gamer = gamers.get(player.getUniqueId());
        if (gamer == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        final GameArena arena = associateTableGamers.get(gamer);
        if (arena == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        return arena.joinTeam(gamer, name);
    }

    public void onPlayerJoinArena(GameArena arena, Gamer player) {
        associateTableGamers.put(player, arena);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        final Gamer gamer = gamers.get(e.getPlayer().getUniqueId());
        if (gamer != null) {
            associateTableGamers.get(gamer).onPlayerDeath(e, gamer);
        }
    }

    public Location getLobby() {
        return lobby;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public static GameCoreBuilder getBuilder(Plugin plugin) {
        return new GameCore(plugin).new GameCoreBuilder();
    }

    public class GameCoreBuilder {
        private GameCoreBuilder() {

        }

        public GameCoreBuilder setArenas(HashMap<String, GameArena> arenas) {
            for (GameArena arena : arenas.values()) {
                for (AbstractGameSpot spot : arena.getGameSpots().values()) {
                    for (SpawnPointAbstract point : spot.getItemsPoints()) {
                        point.setGameSpot(spot);
                    }
                    for (String connectionName : spot.getSerializationConnectionNames()) {
                        spot.addConnection(arena.getGameSpot(connectionName));
                    }
                }
                arena.setGameCore(GameCore.this);
            }
            GameCore.this.arenas = arenas;
            return this;
        }

        public GameCoreBuilder setLobby(Location lobby) {
            GameCore.this.lobby = lobby;
            return this;
        }

        public GameCore build() {
            return GameCore.this;
        }
    }

    public Map<String, GameArena> getArenas() {
        return Collections.unmodifiableMap(arenas);
    }
}
