package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import ru.logonik.unrealminecraft.models.TakeProductsEvent;

public abstract class SpawnPointAbstract {
    protected Location location;
    protected AbstractGameSpot gameSpot;
    private long intervalSpawn;
    private long untilNextSpawnRaid;
    private boolean enabled;
    private boolean isSpawned;

    public SpawnPointAbstract(Location location, AbstractGameSpot gameSpot, long intervalSpawn) {
        setLocation(location);
        this.intervalSpawn = intervalSpawn;
        this.gameSpot = gameSpot;
        this.enabled = false;
    }

    public void tick() {
        if (!enabled) return;
        this.untilNextSpawnRaid--;
        if (this.untilNextSpawnRaid == 0) {
            spawnTick();
            isSpawned = true;
        }
    }

    public void forceSpawnTick() {
        spawnTick();
        isSpawned = true;
        untilNextSpawnRaid = 0;
    }

    public void reset() {
        isSpawned = false;
        untilNextSpawnRaid = intervalSpawn;
        onReset();
    }

    public abstract void onReset();

    protected abstract void spawnTick();

    public void onTryTakeEvent(TakeProductsEvent e) {
        if (isSpawned && e.getGamer().getTeam() != null && e.getGamer().getTeam().equals(gameSpot.getOwner())) {
            onTryTakeByOwner(e);
            if (e.isSuccess()) {
                untilNextSpawnRaid = intervalSpawn;
                isSpawned = false;
            }
        }
    }

    public abstract void onTryTakeByOwner(TakeProductsEvent e);

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        location.setX(location.getBlockX()+0.5);
        location.setY(location.getBlockY()+0.1);
        location.setZ(location.getBlockZ()+0.5);
        location.setPitch(0);
        location.setYaw(0);
        this.location = location;
    }

    public long getIntervalSpawn() {
        return intervalSpawn;
    }

    public void setIntervalSpawn(long intervalSpawn) {
        this.intervalSpawn = intervalSpawn;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public void setSpawned(boolean spawned) {
        isSpawned = spawned;
    }

    public void stop() {
        enabled = false;
        this.untilNextSpawnRaid = this.intervalSpawn;
        onStop();
    }

    public abstract void onStop();

    public void setGameSpot(AbstractGameSpot gameSpot) {
        this.gameSpot = gameSpot;
    }
}
