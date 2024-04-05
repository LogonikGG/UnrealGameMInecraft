package ru.logonik.unrealminecraft.commands;

import org.bukkit.command.CommandSender;
import ru.logonik.unrealminecraft.models.LangCode;
import ru.logonik.unrealminecraft.models.Result;

import java.util.ArrayList;

public abstract class SubCommandAbstract {
    public SubCommandAbstract(String permission, LangCode info) {
        this.permission = permission;
        this.info = info;
    }

    protected String permission;
    protected String usage;
    protected LangCode info;

    public abstract Result onCommand(CommandSender sender, String[] args);

    public String getPermission() {
        return permission;
    }

    public String getUsage() {
        return usage;
    }

    public LangCode getInfo() {
        return info;
    }

    public abstract void onTabComplete(CommandSender sender, String[] args, ArrayList<String> completions);
}
