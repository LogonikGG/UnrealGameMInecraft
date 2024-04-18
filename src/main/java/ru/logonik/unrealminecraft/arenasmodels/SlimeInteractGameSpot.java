package ru.logonik.unrealminecraft.arenasmodels;

import org.bukkit.Location;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.logonik.unrealminecraft.models.GameArena;
import ru.logonik.unrealminecraft.models.Gamer;
import ru.logonik.unrealminecraft.models.Team;

public abstract class SlimeInteractGameSpot implements Listener {
    protected final AbstractGameSpot gameSpot;
    protected final GameArena arena;
    protected Slime slime;
    protected Slime lastSlime;


    public SlimeInteractGameSpot(AbstractGameSpot gameSpot, GameArena arena) {
        this.gameSpot = gameSpot;
        this.arena = arena;
    }

    public abstract void regenerate(Team team);
    public abstract boolean tryRegenerate(Team team);

    @EventHandler
    public void onTakeDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity().getUniqueId().equals(slime.getUniqueId())) {
            Gamer gamer = arena.tryGetGamer(e.getDamager());
            if (gamer == null) {
                return;
            }
            if(gamer.getTeam().equals(gameSpot.getOwner())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSlimeDeath(EntityDeathEvent e) {
        if (slime == null) return;
        if (e.getEntity().getUniqueId().equals(slime.getUniqueId())) {
            arena.gameSpotDestroyed(gameSpot);
            gameSpot.setOwner(null);
        }
    }

    @EventHandler
    public void onSlimeSplits(SlimeSplitEvent e) {
        if (slime == null) return;
        if (e.getEntity().getUniqueId().equals(slime.getUniqueId())
                || e.getEntity().getUniqueId().equals(lastSlime.getUniqueId())) {
            slime = null;
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
