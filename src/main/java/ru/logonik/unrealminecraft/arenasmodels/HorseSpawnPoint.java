package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import ru.logonik.unrealminecraft.models.TakeProductsEvent;

public class HorseSpawnPoint extends SpawnPointAbstract {

    //TODO make always same horse spawn (speed etc)
    //TODO make horse

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
        horse.setInvulnerable(true);
    }

    @Override
    public void onTryTakeByOwner(TakeProductsEvent e) {
        horse.setInvulnerable(false);
        horse.addPassenger(e.getGamer().getPlayer());
        horse = null;
    }

    @Override
    public void onReset() {
        horse.setHealth(0);
    }
}
