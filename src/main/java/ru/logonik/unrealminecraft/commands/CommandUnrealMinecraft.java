package ru.logonik.unrealminecraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import ru.logonik.unrealminecraft.Plugin;
import ru.logonik.unrealminecraft.commands.subcommands.*;
import ru.logonik.unrealminecraft.models.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommandUnrealMinecraft implements CommandExecutor, TabCompleter {

    private final HashMap<String, SubCommandAbstract> commands = new HashMap<>();
    private final Plugin plugin;


    public CommandUnrealMinecraft(Plugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("unreal").setExecutor(this);
        registerCommands(plugin);
    }

    public void registerCommands(Plugin plugin) {
        commands.put("join", new JoinArena(plugin));
        commands.put("team", new ChooseTeam(plugin));
        commands.put("start", new StartGame(plugin));
        commands.put("stop", new StopGame(plugin));
        commands.put("creater", new Creater(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return true;
        }
        final SubCommandAbstract subCommand = commands.get(args[0]);
        if (subCommand == null) {
            return true;
        }
        if (!sender.hasPermission(subCommand.getPermission())) {
            return true;
        }
        Result result = subCommand.onCommand(sender, args);
        if (result != null) {
            sender.sendMessage(plugin.getLocalizedMessage(result));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        ArrayList<String> allowedCommands = new ArrayList<>();
        for (Map.Entry<String, SubCommandAbstract> entry : commands.entrySet()) {
            if(sender.hasPermission(entry.getValue().getPermission())) {
                allowedCommands.add(entry.getKey());
            }
        }

        if (args.length == 0) {
            return StringUtil.copyPartialMatches("", allowedCommands, completions);
        }
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], allowedCommands, completions);
        }

        final SubCommandAbstract subCommand = commands.get(args[0]);
        if (subCommand == null) {
            return completions;
        }
        if (!sender.hasPermission(subCommand.getPermission())) {
            return completions;
        }
        subCommand.onTabComplete(sender, args, completions);
        return completions;
    }
}
