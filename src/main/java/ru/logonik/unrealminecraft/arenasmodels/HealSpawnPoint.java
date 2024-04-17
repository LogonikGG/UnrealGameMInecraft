package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.logonik.unrealminecraft.models.TakeProductsEvent;

public class HealSpawnPoint extends SpawnPointAbstract {

    private Item visualItems;

    public HealSpawnPoint(Location location, AbstractGameSpot gameSpot, long intervalSpawn) {
        super(location, gameSpot, intervalSpawn);
    }


    public HealSpawnPoint(Location location, AbstractGameSpot gameSpot) {
        super(location, gameSpot, 0);
    }

    @Override
    protected void spawnTick() {
        visualItems = location.getWorld().dropItem(location, new ItemStack(Material.GOLDEN_APPLE));
        visualItems.setPickupDelay(Integer.MAX_VALUE);
        visualItems.setTicksLived(Integer.MAX_VALUE);
    }

    @Override
    public void onTryTakeByOwner(TakeProductsEvent e) {
        final Player player = e.getGamer().getPlayer();
        final double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (player.getHealth() == maxHealth) {
            e.setSuccess(false);
            return;
        }
        double newHealth = player.getHealth() + 10;
        if (newHealth >= maxHealth) {
            newHealth = maxHealth;
        }
        player.setHealth(newHealth);
        visualItems.remove();
    }

    @Override
    public void onReset() {
        visualItems.remove();
    }

    @Override
    public void onStop() {
        onReset();
    }
}
