package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;

public class BaseSpot extends AbstractGameSpot {

    public BaseSpot(Location location, String name) {
        super(location, name);
    }

    @Override
    public void onConnectionsChange() {
        canBeDestroyBy.clear();
        canRespawnHere.clear();
        for (AbstractGameSpot connection : connections) {
            canBeDestroyBy.add(connection.getOwner());
        }
        canRespawnHere.add(owner);
    }
}
