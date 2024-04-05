package ru.logonik.unrealminecraft.models;

import org.bukkit.entity.Player;

public class Gamer {
    private Player player;
    private Team team;
    private int score;

    public Gamer(Player player) {
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
