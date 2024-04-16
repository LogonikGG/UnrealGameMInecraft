package ru.logonik.unrealminecraft.commands.subcommands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.arenasmodels.*;
import ru.logonik.unrealminecraft.commands.SubCommandAbstract;
import ru.logonik.unrealminecraft.models.GameArena;
import ru.logonik.unrealminecraft.models.LangCode;
import ru.logonik.unrealminecraft.models.Result;
import ru.logonik.unrealminecraft.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public class Creater extends SubCommandAbstract {
    private final Plugin plugin;
    private final HashMap<UUID, StateDataStorage> data;

    private final String CONTEXT = "context";
    private final String ARENA = "arena";
    private final String TEAM = "team";
    private final String SPOT = "spot";
    private final String POINT = "point";
    private final String CONNECTION = "connection";
    private final String REMOVE = "remove";
    private final String INFO = "info";
    private final List<String> firstArgs = Arrays.asList(CONTEXT, ARENA, TEAM, SPOT, POINT, CONNECTION, REMOVE, INFO);
    private final List<String> secondArgsContext = Arrays.asList(ARENA, SPOT, POINT);

    private final String BASESPOT = "basespot";
    private final String MIDDLESPOT = "middlespot";
    private final List<String> secondArgsSpot = Arrays.asList(BASESPOT, MIDDLESPOT);

    private final String SPAWN_PLACE = "spawn_place";
    private final String ARMOR_POINT = "armor_point";
    private final String HEAL_POINT = "heal_point";
    private final String HORSE_POINT = "horse_point";
    private final String ITEM_POINT = "item_point";
    private final List<String> secondArgsPoint = Arrays.asList(SPAWN_PLACE, ARMOR_POINT, HEAL_POINT, HORSE_POINT);
    private final List<String> secondArgsRemove = Arrays.asList(ARENA, TEAM, SPOT, SPAWN_PLACE, ITEM_POINT, CONNECTION);

    public Creater(Plugin plugin) {
        super("unrealminecraft.admin", LangCode.UNKNOWN_ERROR);
        this.plugin = plugin;
        this.data = new HashMap<>();
    }


    // first we set context ARENA, SPOT or Point
    // then we can create SPOT (if ARENA context is defined) or Point (if SPOT context is defined)
    // ex: /creater context arena <name> --> /creater context spot <name> --> /creater connection <name of another spot>

    @Override
    public Result onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        StateDataStorage storage = getDataOrInit(player.getUniqueId());
        switch (args[1]) {
            case CONTEXT: {
                if (args.length < 4) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                switch (args[2]) {
                    case ARENA: {
                        String name = Util.getString(args, 3);
                        GameArena arena = plugin.getGameCore().getArena(name);
                        if (arena == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        storage.arena = arena;
                        storage.spot = null;
                        storage.point = null;
                        return new Result(true, LangCode.SUCCESS);
                    }
                    case SPOT: {
                        if (storage.arena == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        String name = Util.getString(args, 3);
                        AbstractGameSpot gameSpot = storage.arena.getGameSpot(name);
                        if (gameSpot == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        storage.spot = gameSpot;
                        storage.point = null;
                        return new Result(true, LangCode.SUCCESS);
                    }
                    case POINT: {
                        if (storage.arena == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        if (storage.spot == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        int id;
                        try {
                            id = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        List<SpawnPointAbstract> points = storage.spot.getItemsPoints();
                        if (points.size() <= id || id < 0) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        storage.point = points.get(id);
                        return new Result(true, LangCode.SUCCESS);
                    }
                }
            }
            case ARENA: {
                String name = Util.getString(args, 2);
                return plugin.getGameCore().createArena(player.getLocation(), name);
            }
            case TEAM: {
                if (storage.arena == null) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                if (!(storage.spot instanceof BaseSpot)) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                String name = Util.getString(args, 2);
                if(name.isEmpty()) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                return storage.arena.createTeam(name, (BaseSpot) storage.spot);
            }
            case SPOT: {
                if (storage.arena == null) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                if (args.length < 4) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                String name = Util.getString(args, 3);
                switch (args[2]) {
                    case BASESPOT: {
                        return storage.arena.createGameSpot(name, player.getLocation(), true);
                    }
                    case MIDDLESPOT: {
                        return storage.arena.createGameSpot(name, player.getLocation(), false);
                    }
                }
            }
            case POINT: {
                if (storage.arena == null) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                if (storage.spot == null) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                if (args.length < 3) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                switch (args[2]) {
                    case SPAWN_PLACE: {
                        storage.spot.addSpawnLocation(player.getLocation());
                        return new Result(false, LangCode.SUCCESS);
                    }
                    case ARMOR_POINT: {
                        ItemStack is = player.getInventory().getItemInMainHand();
                        if (is == null || is.getType() == Material.AIR) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        ArrayList<ItemStack> list = new ArrayList<>();
                        storage.spot.addItemPoint(new ArmorSpawnPoint(player.getLocation(), storage.spot, list));
                        return new Result(false, LangCode.SUCCESS);
                    }
                    case HEAL_POINT: {
                        storage.spot.addItemPoint(new HealSpawnPoint(player.getLocation(), storage.spot));
                        return new Result(false, LangCode.SUCCESS);
                    }
                    case HORSE_POINT: {
                        storage.spot.addItemPoint(new HorseSpawnPoint(player.getLocation(), storage.spot));
                        return new Result(false, LangCode.SUCCESS);
                    }
                }
            }
            case CONNECTION: {
                if (args.length < 3) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                if (storage.arena == null) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                if (storage.spot == null) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                String name = Util.getString(args, 2);
                return storage.arena.addConnections(storage.spot, name);
            }
            case REMOVE: {
                if (args.length < 4) {
                    return new Result(false, LangCode.UNKNOWN_ERROR);
                }
                switch (args[2]) {
                    case ARENA: {
                        String name = Util.getString(args, 3);
                        return plugin.getGameCore().removeArena(name);
                    }
                    case TEAM: {
                        if (storage.arena == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        String name = Util.getString(args, 3);
                        return storage.arena.removeTeam(name);
                    }
                    case SPOT: {
                        if (storage.arena == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        String name = Util.getString(args, 3);
                        return storage.arena.removeGameSpot(name);
                    }
                    case SPAWN_PLACE: {
                        if (storage.arena == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        if (storage.spot == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        int id;
                        try {
                            id = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        if (!storage.spot.removeSpawnLocation(id)) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        return new Result(true, LangCode.SUCCESS);
                    }
                    case ITEM_POINT: {
                        if (storage.arena == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        if (storage.spot == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        int id;
                        try {
                            id = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        if (!storage.spot.removeItemPoint(id)) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        return new Result(true, LangCode.SUCCESS);
                    }
                    case CONNECTION: {
                        if (storage.arena == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        if (storage.spot == null) {
                            return new Result(false, LangCode.UNKNOWN_ERROR);
                        }
                        String name = Util.getString(args, 3);
                        return storage.arena.removeConnection(storage.spot, name);
                    }
                }
            }
            case INFO: {
                player.sendMessage("Все арены: ");
                String arenasNames = Util.getString(plugin.getGameCore().getArenasNames(), ", ");
                player.sendMessage(arenasNames);
                if (storage.arena != null) {
                    player.sendMessage("Название арены: " + storage.arena.getName());
                    player.sendMessage("Название спотов: " + String.join(", ", storage.arena.getGameSpots().keySet()));
                    player.sendMessage("Название команд: " + String.join(", ", storage.arena.getTeamList()));
                }
                if (storage.spot != null) {
                    player.sendMessage("Название спота: " + storage.spot.getName());
                    List<SpawnPointAbstract> points = storage.spot.getItemsPoints();
                    player.sendMessage("Все соединения: "
                            + Util.getString(storage.spot.getConnections()
                            .stream().map(AbstractGameSpot::getName)
                            .collect(Collectors.toList()), ", "));
                    player.sendMessage("Все точки игроков: " + Util.getString(storage.spot.getSpawns(), "\n"));
                    player.sendMessage("Все точки предметов: " + Util.getString(points, "\n"));
                }
                return null;
            }
            default:
                return new Result(false, LangCode.UNKNOWN_ERROR);
        }
    }

    @Override
    public void onTabComplete(CommandSender sender, String[] args, ArrayList<String> completions) {
        if (!(sender instanceof Player)) return;

        StateDataStorage storage = getDataOrInit(((Player) sender).getUniqueId());
        switch (args.length) {
            case 1: {
                StringUtil.copyPartialMatches("", firstArgs, completions);
                return;
            }
            case 2: {
                StringUtil.copyPartialMatches(args[1], firstArgs, completions);
                return;
            }
            case 3: {
                switch (args[1]) {
                    case CONTEXT: {
                        StringUtil.copyPartialMatches(args[2], secondArgsContext, completions);
                        return;
                    }
                    case SPOT: {
                        StringUtil.copyPartialMatches(args[2], secondArgsSpot, completions);
                        return;
                    }
                    case POINT: {
                        StringUtil.copyPartialMatches(args[2], secondArgsPoint, completions);
                        return;
                    }
                    case REMOVE: {
                        StringUtil.copyPartialMatches(args[2], secondArgsRemove, completions);
                        return;
                    }
                    case CONNECTION: {
                        if (storage.arena == null || storage.spot == null) {
                            return;
                        }
                        StringUtil.copyPartialMatches(args[2], storage.arena.getGameSpots().keySet(), completions);
                        return;
                    }
                }
            }
            case 4: {
                switch (args[1]) {
                    case CONTEXT: {
                        switch (args[2]) {
                            case ARENA: {
                                StringUtil.copyPartialMatches(args[3], plugin.getGameCore().getArenasNames(), completions);
                                return;
                            }
                            case SPOT: {
                                if (storage.arena == null) {
                                    return;
                                }
                                StringUtil.copyPartialMatches(args[3], storage.arena.getGameSpots().keySet(), completions);
                                return;
                            }
                        }
                    }
                    case REMOVE: {
                        switch (args[2]) {
                            case ARENA: {
                                StringUtil.copyPartialMatches(args[3], plugin.getGameCore().getArenasNames(), completions);
                                return;
                            }
                            case TEAM: {
                                if (storage.arena == null) {
                                    return;
                                }
                                StringUtil.copyPartialMatches(args[3], storage.arena.getTeamList(), completions);
                                return;
                            }
                            case SPOT: {
                                if (storage.arena == null) {
                                    return;
                                }
                                StringUtil.copyPartialMatches(args[3], storage.arena.getGameSpots().keySet(), completions);
                                return;
                            }
                            case SPAWN_PLACE: {
                                if (storage.arena == null || storage.spot == null) {
                                    return;
                                }
                                completions.add(String.valueOf(storage.spot.getSpawns().size()));
                                return;
                            }
                            case ITEM_POINT: {
                                if (storage.arena == null || storage.spot == null) {
                                    return;
                                }
                                completions.add(String.valueOf(storage.spot.getItemsPoints().size()));
                                return;
                            }
                            case CONNECTION: {
                                if (storage.arena == null || storage.spot == null) {
                                    return;
                                }
                                StringUtil.copyPartialMatches(args[3], storage.arena.getGameSpots().keySet(), completions);
                            }
                        }
                    }
                }
            }
        }
    }

    private StateDataStorage getDataOrInit(UUID uuid) {
        StateDataStorage stateDataStorage = data.get(uuid);
        if (stateDataStorage == null) {
            stateDataStorage = new StateDataStorage();
            data.put(uuid, stateDataStorage);
        }
        return stateDataStorage;
    }
}
