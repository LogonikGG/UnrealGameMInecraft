package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;

import java.util.ArrayList;

public class MiddleSpot extends AbstractGameSpot {

    public MiddleSpot(Location location, String name) {
        super(location, name);
    }

    @Override
    public void onConnectionsChange() {
        canRespawnHere.clear();
        canBeDestroyBy.clear();

        ArrayList<AbstractGameSpot> cashRecursion = new ArrayList<>();
        cashRecursion.add(this);

        for (AbstractGameSpot connection : connections) {
            if (connection instanceof BaseSpot) {
                if (connection.getOwner() != null) {
                    canBeDestroyBy.add(connection.getOwner());
                    break;
                }
            }
            final AbstractGameSpot base = tryFindConnectionToBaseRecursion(connection, cashRecursion);
            if (base != null) {
                if (connection.getOwner() != null) {
                    canBeDestroyBy.add(base.getOwner());
                }
            }
        }
        //todo respawn only if we have connection from base
        canRespawnHere.add(owner);
    }

    private AbstractGameSpot tryFindConnectionToBaseRecursion(AbstractGameSpot gameSpot, ArrayList<AbstractGameSpot> cash) {
        cash.add(gameSpot);
        for (AbstractGameSpot abstractGameSpot : gameSpot.connections) {
            if (cash.contains(abstractGameSpot)) break;
            if (!abstractGameSpot.owner.equals(gameSpot.owner)) break;
            if (gameSpot instanceof BaseSpot) {
                return gameSpot;
            }
            return tryFindConnectionToBaseRecursion(abstractGameSpot, cash);
        }
        return null;
    }
}
