package ru.logonik.unrealminecraft.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;
import ru.logonik.unrealminecraft.arenasmodels.*;

import java.util.*;

public class GameArena implements Listener {
    private final HashMap<Gamer, Team> players;
    private HashMap<String, AbstractGameSpot> spots;
    private final HashMap<AbstractGameSpot, SlimeInteractGameSpot> interactedSpots;
    private GameCore gameCore;
    private final ArrayList<Team> teams;
    private final String name;
    private final Location arenaLobby;
    private BukkitTask gameTickTask;

    public GameArena(GameCore core, Location location, String name) {
        this.gameCore = core;
        this.spots = new HashMap<>();
        this.players = new HashMap<>();
        this.teams = new ArrayList<>();
        this.interactedSpots = new HashMap<>();
        this.name = name;
        this.arenaLobby = location;
    }

    public Result startGame() {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        return new Result(true, LangCode.SUCCESS);
    }

    private void start() {
        for (Gamer gamer : players.keySet()) {
            final Player player = gamer.getPlayer();
            player.teleport(getRespawnLocation(gamer));
            player.sendMessage("СТАРТ");
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setSaturation(5);
            player.setFoodLevel(20);
        }
        for (AbstractGameSpot value : spots.values()) {
            for (SpawnPointAbstract spawnPoint : value.getItemsPoints()) {
                spawnPoint.setEnabled(true);
                spawnPoint.forceSpawnTick();
            }
            SlimeInteractGameSpot interacted = new SlimeInteractGameSpot(value, this);
            gameCore.getPlugin().getServer().getPluginManager().registerEvents(interacted, gameCore.getPlugin());
            interactedSpots.put(value, interacted);
        }
        gameTickTask = gameCore.getPlugin().getServer().getScheduler().runTaskTimer(gameCore.getPlugin(), this::gameTick, 2, 1);
    }

    private void gameTick() {
        for (AbstractGameSpot value : spots.values()) {
            for (SpawnPointAbstract spawnPoint : value.getItemsPoints()) {
                spawnPoint.tick();
            }
        }
        for (Gamer gamer : players.keySet()) {
            Location location = gamer.getPlayer().getLocation();
            for (AbstractGameSpot spot : spots.values()) {
                for (SpawnPointAbstract point : spot.getItemsPoints()) {
                    if(location.distance(point.getLocation())<2) {
                        point.onTryTakeEvent(new TakeProductsEvent(gamer));
                    }
                }
            }
        }

    }

    public Result stopGame() {
        try {
            stop();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        gameTickTask.cancel();
        return new Result(true, LangCode.SUCCESS);
    }

    private void stop() {
        for (Gamer gamer : players.keySet()) {
            final Player player = gamer.getPlayer();
            player.teleport(gameCore.getLobby());
            player.sendMessage("STOP");
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setSaturation(5);
            player.setFoodLevel(20);
        }
        for (AbstractGameSpot value : spots.values()) {
            for (SpawnPointAbstract spawnPoint : value.getItemsPoints()) {
                spawnPoint.reset();
            }
        }
        for (SlimeInteractGameSpot value : interactedSpots.values()) {
            HandlerList.unregisterAll(value);
        }
    }

    public Result joinArena(Gamer player) {
        Objects.requireNonNull(player);
        players.put(player, null);
        player.getPlayer().teleport(arenaLobby);
        gameCore.onPlayerJoinArena(this, player);
        return new Result(true, LangCode.SUCCESS);
    }

    public Result joinTeam(Gamer player, Team team) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(team);
        players.put(player, team);
        return new Result(true, LangCode.SUCCESS);
    }

    public Result createTeam(String name) {
        for (Team team : teams) {
            if (team.getName().equals(name)) {
                return new Result(false, LangCode.UNKNOWN_ERROR);
            }
        }
        teams.add(new Team(name));
        return new Result(true, LangCode.SUCCESS);
    }

    public Result removeTeam(String name) {
        for (Team team : teams) {
            if (team.getName().equals(name)) {
                teams.remove(team);
                return new Result(true, LangCode.SUCCESS);
            }
        }
        return new Result(false, LangCode.UNKNOWN_ERROR);
    }

    public Result joinTeam(Gamer player, String teamName) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(teamName);
        Team team = getTeam(teamName);
        if (team == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        players.put(player, team);
        return new Result(true, LangCode.SUCCESS);
    }

    public Result leaveTeam(Gamer gamer) {
        Objects.requireNonNull(gamer);
        final Team team = players.get(gamer);
        if (team == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        players.put(gamer, null);
        return new Result(true, LangCode.SUCCESS);
    }

    public Team getTeam(String teamName) {
        for (Team team : teams) {
            if (team.getName().equals(teamName)) {
                return team;
            }
        }
        return null;
    }

    public Result addConnections(String spot1, String spot2) {
        AbstractGameSpot gameSpot1 = spots.get(spot1);
        AbstractGameSpot gameSpot2 = spots.get(spot2);
        if (gameSpot1 == null || gameSpot2 == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        addConnections(gameSpot1, gameSpot2);
        return new Result(true, LangCode.SUCCESS);
    }

    public Result addConnections(AbstractGameSpot gameSpot1, String name) {
        AbstractGameSpot gameSpot2 = spots.get(name);
        if (gameSpot1 == null || gameSpot2 == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        addConnections(gameSpot1, gameSpot2);
        return new Result(true, LangCode.SUCCESS);
    }

    public void addConnections(AbstractGameSpot spot1, AbstractGameSpot spot2) {
        Objects.requireNonNull(spot1);
        Objects.requireNonNull(spot2);
        spot1.addConnection(spot2);
        spot2.addConnection(spot1);
    }

    public Result removeConnection(AbstractGameSpot gameSpot1, String name) {
        AbstractGameSpot gameSpot2 = spots.get(name);
        if (gameSpot1 == null || gameSpot2 == null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        removeConnections(gameSpot1, gameSpot2);
        return new Result(true, LangCode.SUCCESS);
    }

    public void removeConnections(AbstractGameSpot spot1, AbstractGameSpot spot2) {
        Objects.requireNonNull(spot1);
        Objects.requireNonNull(spot2);
        spot1.removeConnection(spot2);
        spot2.removeConnection(spot1);
    }

    public void teleportAll(Location location) {
        for (Gamer gamer : players.keySet()) {
            gamer.getPlayer().teleport(location);
        }
    }

    public Map<String, AbstractGameSpot> getGameSpots() {
        return Collections.unmodifiableMap(spots);
    }

    public AbstractGameSpot getGameSpot(String name) {
        return spots.get(name);
    }

    public void onPlayerDeath(PlayerRespawnEvent e, Gamer gamer) {
        e.setRespawnLocation(getRespawnLocation(gamer));
    }

    public Location getRespawnLocation(Gamer gamer) {
        for (AbstractGameSpot value : spots.values()) {
            for (Team team : value.getCanRespawnHere()) {
                if (team.equals(gamer.getTeam())) {
                    if (value.getSpawns().size() == 0) continue;
                }
                int i = new Random().nextInt(value.getSpawns().size());
                return value.getSpawns().get(i);
            }
        }
        Bukkit.getLogger().severe("No spawn location found for gamer ");
        return arenaLobby;
    }

    public void gameSpotDestroyed(AbstractGameSpot gameSpot) {
        connectionsChanges();
        broadcastToPlayers(gameSpot.getLocation().toString() + " сломан");
    }

    private void connectionsChanges() {
        for (AbstractGameSpot value : spots.values()) {
            value.onConnectionsChange();
        }
    }

    private void broadcastToPlayers(String message) {
        for (Gamer gamer : players.keySet()) {
            gamer.getPlayer().sendMessage(message);
        }
    }

    public GameCore getGameCore() {
        return gameCore;
    }

    public void gameSpotGenerated(AbstractGameSpot gameSpot) {
        connectionsChanges();
        broadcastToPlayers(gameSpot.getLocation().toString() + " сгенерирован");
    }

    public Result createGameSpot(String name, Location location, boolean isBase) {
        AbstractGameSpot test = spots.get(name);
        if (test != null) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        AbstractGameSpot abstractGameSpot;
        if (isBase) {
            abstractGameSpot = new BaseSpot(location, name);
        } else {
            abstractGameSpot = new MiddleSpot(location, name);
        }
        spots.put(name, abstractGameSpot);
        return new Result(true, LangCode.SUCCESS);
    }

    public Result removeGameSpot(String name) {
        AbstractGameSpot abstractGameSpot = spots.remove(name);
        if (abstractGameSpot != null) {
            return new Result(true, LangCode.SUCCESS);
        } else {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
    }

    public String getName() {
        return name;
    }

    public Location getArenaLobby() {
        return arenaLobby;
    }

    public void setSpots(HashMap<String, AbstractGameSpot> spots) {
        this.spots = spots;
    }

    public void setGameCore(GameCore gameCore) {
        this.gameCore = gameCore;
    }
}