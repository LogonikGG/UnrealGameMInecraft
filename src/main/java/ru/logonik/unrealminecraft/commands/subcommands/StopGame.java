package ru.logonik.unrealminecraft.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.commands.SubCommandAbstract;
import ru.logonik.unrealminecraft.models.LangCode;
import ru.logonik.unrealminecraft.models.Result;

import java.util.ArrayList;

public class StopGame extends SubCommandAbstract {
    private final Plugin plugin;

    public StopGame(Plugin plugin) {
        super("unrealminectaft.admin", LangCode.UNKNOWN_ERROR);
        this.plugin = plugin;
    }


    @Override
    public Result onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        return plugin.getGameCore().getArena((Player) sender).stopGame();
    }

    @Override
    public void onTabComplete(CommandSender sender, String[] args, ArrayList<String> completions) {
    }
}
