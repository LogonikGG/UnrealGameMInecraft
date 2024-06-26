package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import ru.logonik.unrealminecraft.models.TakeProductsEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArmorSpawnPoint extends SpawnPointAbstract {
    private final ArrayList<ItemStack> itemStacks;
    private final ArrayList<Item> droppedItems;

    public ArmorSpawnPoint(Location location, AbstractGameSpot gameSpot) {
        super(location, gameSpot, 0);
        this.itemStacks = new ArrayList<>();
        this.droppedItems = new ArrayList<>();
    }

    public ArmorSpawnPoint(Location location, AbstractGameSpot gameSpot, long intervalSpawn) {
        super(location, gameSpot, intervalSpawn);
        this.itemStacks = new ArrayList<>();
        this.droppedItems = new ArrayList<>();
    }

    public ArmorSpawnPoint(Location location, AbstractGameSpot gameSpot, ArrayList<ItemStack> itemStacks) {
        super(location, gameSpot, 0);
        itemStacks.removeIf(itemStack -> itemStack == null || itemStack.getType() == Material.AIR);
        this.itemStacks = itemStacks;
        this.droppedItems = new ArrayList<>();
    }

    public ArmorSpawnPoint(Location location, AbstractGameSpot gameSpot, ArrayList<ItemStack> itemStacks, long intervalSpawn) {
        super(location, gameSpot, intervalSpawn);
        this.itemStacks = itemStacks;
        this.droppedItems = new ArrayList<>();
    }

    public void addItem(ItemStack is) {
        itemStacks.add(is);
    }

    public List<ItemStack> getItemStacks() {
        return Collections.unmodifiableList(itemStacks);
    }

    @Override
    protected void spawnTick() {
        for (ItemStack itemStack : itemStacks) {
            final Item item = location.getWorld().dropItem(location, itemStack.clone());
            item.setTicksLived(Integer.MAX_VALUE);
            item.setPickupDelay(Integer.MAX_VALUE);
            droppedItems.add(item);
        }
    }

    @Override
    public void onTryTakeByOwner(TakeProductsEvent e) {
        for (Item visualItem : droppedItems) {
            visualItem.setPickupDelay(0);
        }
    }

    @Override
    public void onStop() {
        onReset();
    }

    @Override
    public void onReset() {
        for (Item visualItem : droppedItems) {
            visualItem.remove();
        }
    }
}
