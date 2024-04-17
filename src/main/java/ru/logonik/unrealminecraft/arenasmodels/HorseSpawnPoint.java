package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import ru.logonik.unrealminecraft.models.TakeProductsEvent;

public class HorseSpawnPoint extends SpawnPointAbstract implements Listener {

    private Horse horse;

    public HorseSpawnPoint(Location location, AbstractGameSpot gameSpot) {
        super(location, gameSpot, 0);
    }

    public HorseSpawnPoint(Location location, AbstractGameSpot gameSpot, long intervalSpawn) {
        super(location, gameSpot, intervalSpawn);
    }

    @Override
    protected void spawnTick() {
        horse = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
        horse.setCustomName("MASHINA");
        horse.setAdult();
        horse.setSilent(true);
        horse.setCollidable(false);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setInvulnerable(true);
    }

    @Override
    public void onTryTakeByOwner(TakeProductsEvent e) {
        horse.setInvulnerable(false);
        horse.addPassenger(e.getGamer().getPlayer());
        horse.setCollidable(true);
        horse.setTamed(true);
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.29);
        horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
        horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(0.8);
        horse = null;
    }

    @EventHandler
    public void onHorseDeath(EntityDeathEvent e) {
        if (horse == null) return;
        if (e.getEntity().getUniqueId().equals(horse.getUniqueId())) {
            reset();
        }
    }

    @Override
    public void onReset() {
        horse.setHealth(0);
    }

    @Override
    public void onStop() {
        onReset();
    }
}
