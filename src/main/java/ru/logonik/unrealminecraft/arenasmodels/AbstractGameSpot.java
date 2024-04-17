package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import ru.logonik.unrealminecraft.models.Result;
import ru.logonik.unrealminecraft.models.Team;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractGameSpot {
    protected final ArrayList<AbstractGameSpot> connections;
    protected final Location location;
    @Nullable
    protected Team owner;
    private ArrayList<Location> spawns;
    protected ArrayList<SpawnPointAbstract> items;
    protected final ArrayList<Team> canBeDestroyBy;
    protected final ArrayList<Team> canRespawnHere;
    protected final String name;
    private List<String> serializationConnectionNames;

    public AbstractGameSpot(Location location, String name) {
        this.location = location;
        this.spawns = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.items = new ArrayList<>();
        this.canBeDestroyBy = new ArrayList<>();
        this.canRespawnHere = new ArrayList<>();
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    @Nullable
    public Team getOwner() {
        return owner;
    }

    public void setOwner(@Nullable Team owner) {
        this.owner = owner;
    }

    public ArrayList<Location> getSpawns() {
        return spawns;
    }

    public void addConnection(AbstractGameSpot spot) {
        if(this.equals(spot)) {
            return;
        }
        if(connections.contains(spot)) {
            return;
        }
        this.connections.add(spot);
    }

    public void removeConnection(AbstractGameSpot spot) {
        this.connections.remove(spot);
    }

    public boolean canBeDestroyBy(Team team) {
        return canBeDestroyBy.contains(team);
    }

    public List<Team> getCanRespawnHere() {
        return Collections.unmodifiableList(canRespawnHere);
    }

    public List<SpawnPointAbstract> getItemsPoints() {
        return Collections.unmodifiableList(items);
    }

    public abstract void onConnectionsChange();

    public void setSpawnLocations(ArrayList<Location> spawns) {
        this.spawns = spawns;
    }

    public void addSpawnLocations(Location... locations) {
        for (Location location : locations) {
            addSpawnLocation(location);
        }
    }

    public void addSpawnLocation(Location location) {
        spawns.add(location);
    }
    public boolean removeSpawnLocation(int i) {
        if(i < 0 || i >= spawns.size()) {
            spawns.remove(i);
            return true;
        } else {
            return false;
        }
    }

    public void setItemPoints(ArrayList<SpawnPointAbstract> itemsPoints) {
        this.items = itemsPoints;
    }

    public void addItemPoint(SpawnPointAbstract itemPoint) {
        items.add(itemPoint);
    }

    public boolean removeItemPoint(int id) {
        if(id < 0 || id >= items.size()) {
            items.remove(id);
            return true;
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public List<AbstractGameSpot> getConnections() {
        return Collections.unmodifiableList(connections);
    }

    public List<String> getSerializationConnectionNames() {
        return serializationConnectionNames;
    }

    public void setSerializationConnectionNames(List<String> serializationConnectionNames) {
        this.serializationConnectionNames = serializationConnectionNames;
    }
}
