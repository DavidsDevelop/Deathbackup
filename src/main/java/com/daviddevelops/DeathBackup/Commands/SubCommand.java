package com.daviddevelops.DeathBackup.Commands;

import org.bukkit.entity.Player;

public abstract class SubCommand {

    //name of the subcommand ex. /cmd <subcommand> <-- that
    public abstract String getName();

    //ex. "This is a subcommand that is now explaining what it does"
    public abstract String getDescription();

    //How to use command ex. /cmd edit <name>
    public abstract String getSyntax();

    //code for the subcommand
    public abstract void perform(Player player, String args[]);

}