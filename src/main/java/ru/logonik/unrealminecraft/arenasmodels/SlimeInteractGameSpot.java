package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.logonik.unrealminecraft.models.GameArena;
import ru.logonik.unrealminecraft.models.Gamer;
import ru.logonik.unrealminecraft.models.Team;

public class SlimeInteractGameSpot implements Listener {
    private final AbstractGameSpot gameSpot;
    private final GameArena arena;
    private Slime slime;


    public SlimeInteractGameSpot(AbstractGameSpot gameSpot, GameArena arena) {
        this.gameSpot = gameSpot;
        this.arena = arena;
    }

    public void regenerate(Team team) {
        gameSpot.setOwner(team);

        final Location location = gameSpot.getLocation();
        if (slime != null) {
            slime.setHealth(0);
        }
        slime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
        slime.setSize(4);
        slime.setAI(false);
        slime.setSilent(true);
        slime.setCustomName(gameSpot.getOwner().getName());
        arena.gameSpotGenerated(gameSpot);
    }

    @EventHandler
    public void onSlimeDeath(EntityDeathEvent e) {
        if (e.getEntity().getUniqueId().equals(slime.getUniqueId())) {
            gameSpot.setOwner(null);
            arena.gameSpotDestroyed(gameSpot);
        }
    }

    @EventHandler
    public void onSlimeSplits(SlimeSplitEvent e) {
        if (e.getEntity().getUniqueId().equals(slime.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        Gamer gamer = arena.getGameCore().tryGetGamer(e.getPlayer());
        if (gamer != null && gamer.getTeam() != null) {
            if (gameSpot.getOwner() == null) {
                Location to = e.getTo();
                double distance = gameSpot.getLocation().distance(to);
                if (distance < 10) {
                    regenerate(gamer.getTeam());
                }
            }
        }
    }
}
