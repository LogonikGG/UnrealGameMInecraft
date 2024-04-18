package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import ru.logonik.unrealminecraft.models.GameArena;
import ru.logonik.unrealminecraft.models.Team;

import java.util.Objects;

public class MiddleSpotSlime extends SlimeInteractGameSpot{
    public MiddleSpotSlime(AbstractGameSpot gameSpot, GameArena arena) {
        super(gameSpot, arena);
    }

    @Override
    public boolean tryRegenerate(Team team) {
        regenerate(team);
        return true;
    }

    @Override
    public void regenerate(Team team) {
        Objects.requireNonNull(team);
        gameSpot.setOwner(team);

        final Location location = gameSpot.getLocation();
        if (slime != null) {
            slime.setHealth(0);
        }
        lastSlime = slime;
        slime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
        slime.setSize(4);
        slime.setAI(false);
        slime.setSilent(true);
        slime.setCustomName(team.getName());
        arena.gameSpotGenerated(gameSpot);
    }
}
