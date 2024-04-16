package ru.logonik.unrealminecraft.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.commands.SubCommandAbstract;
import ru.logonik.unrealminecraft.models.GameArena;
import ru.logonik.unrealminecraft.models.LangCode;
import ru.logonik.unrealminecraft.models.Result;
import ru.logonik.unrealminecraft.util.Util;

import java.util.ArrayList;

public class ChooseTeam extends SubCommandAbstract {
    private final Plugin plugin;

    public ChooseTeam(Plugin plugin) {
        super("unrealminecraft.user", LangCode.UNKNOWN_ERROR);
        this.plugin = plugin;
    }

    @Override
    public Result onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        if (args.length < 2) {
            return new Result(false, LangCode.UNKNOWN_ERROR);
        }
        String name = Util.getString(args, 1);
        return plugin.getGameCore().joinTeam(((Player) sender).getPlayer(), name);
    }

    @Override
    public void onTabComplete(CommandSender sender, String[] args, ArrayList<String> completions) {
        if (!(sender instanceof Player)) {
            return;
        }
        GameArena arena = plugin.getGameCore().getArena((Player) sender);
        if (arena == null) {
            return;
        }
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], arena.getTeamList(), completions);
        }
    }
}
