package ru.logonik.unrealminecraft.util;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import ru.logonik.unrealminecraft.arenasmodels.SpawnPointAbstract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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


    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static void normalizePlayer(Player player) {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }
        player.setSaturation(5);
        player.setFoodLevel(20);
        player.getInventory().clear();
    }
}
