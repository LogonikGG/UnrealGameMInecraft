package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import ru.logonik.unrealminecraft.models.Team;

public class BaseSpot extends AbstractGameSpot {

    public BaseSpot(Location location, String name) {
        super(location, name);
    }

    @Override
    public void onConnectionsChange() {
        canBeDestroyBy.clear();
        canRespawnHere.clear();
        for (AbstractGameSpot connection : connections) {
            Team owner = connection.getOwner();
            if (owner != null) {
                canBeDestroyBy.add(owner);
            }
        }
        if (owner != null) {
            canRespawnHere.add(owner);
        }
    }
}
