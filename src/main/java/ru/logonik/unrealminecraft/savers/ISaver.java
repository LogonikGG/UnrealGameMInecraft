package ru.logonik.unrealminecraft.savers;

import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.models.GameCore;

public interface ISaver {
    void save(GameCore gameCore);
    GameCore load(Plugin plugin);
}
