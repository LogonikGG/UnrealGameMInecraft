package ru.logonik.unrealminecraft.models;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {
    private String name;
    private int score;
    private ArrayList<Gamer> gamers;

    public Team(String name) {
        this.gamers = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void leaveAll() {
        gamers.clear();
    }
    public void leave(Gamer gamer) {
        gamers.remove(gamer);
    }
    public void join(Gamer gamer) {
        if(gamers.contains(gamer)) {
            throw new IllegalArgumentException("Gamer cannot join twice in same team");
        }
        gamers.add(gamer);
    }

    public List<Gamer> getGamers() {
        return Collections.unmodifiableList(gamers);
    }
}
