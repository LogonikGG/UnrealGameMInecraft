package ru.logonik.unrealminecraft.util;

import org.bukkit.Location;
import ru.logonik.unrealminecraft.arenasmodels.SpawnPointAbstract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

public class Util {


    //unreal join <name>
    public static String getString(String[] args, int from) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = from; i < args.length; i++) {
            joiner.add(args[i]);
        }
        return joiner.toString();
    }

    public static String getString(Collection<String> parts, String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (String part : parts) {
            joiner.add(part);
        }
        return joiner.toString();
    }

    public static String getString(ArrayList<Location> spawns, String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (Location part : spawns) {
            joiner.add(getReadableLocation(part));
        }
        return joiner.toString();
    }

    private static String getReadableLocation(Location location) {
        return location.getWorld().getName()+" x:" + location.getBlockX()+" y:" + location.getBlockY()+" z:" + location.getBlockZ();
    }

    public static String getString(List<SpawnPointAbstract> points, String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (SpawnPointAbstract point : points) {
            joiner.add(point.getClass().getSimpleName()
                    + " Ядро: " + getReadableLocation(point.getLocation())
                    + " Интервал: " + point.getIntervalSpawn()
                    + " Заспавнен: " + point.isSpawned());
        }
        return joiner.toString();
    }
}
