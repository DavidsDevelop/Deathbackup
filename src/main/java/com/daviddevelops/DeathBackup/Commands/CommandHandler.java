package com.daviddevelops.DeathBackup.Commands;

import com.daviddevelops.DeathBackup.GUIs.PlayerDeathInventory;
import com.daviddevelops.DeathBackup.GUIs.PlayerSelectionInventory;
import com.daviddevelops.DeathBackup.Utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor {

    Utility ut;

    private List<SubCommand> subcommands = new ArrayList<>();

    public CommandHandler(Utility ut){
        this.ut = ut;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(args.length == 0){
                // Open menu
                PlayerSelectionInventory inv = new PlayerSelectionInventory();
                inv.INVENTORY.open(player);
                return true;
            } else if (args.length == 1){
                // Open players direct menu
                PlayerDeathInventory inv = new PlayerDeathInventory();
                if(inv.setPlayer(args[0], player)){
                    player.sendMessage(ut.translateColors("&fUsage &7- &d/adbp [player] (/adbp for menu)"));
                }
                return true;
            } else {
                // Give syntax
                player.sendMessage(ut.translateColors("&fUsage &7- &d/adbp [player] (/adbp for menu)"));
            }
        }
        return true;
    }


//    --[ Getter & Setters  ]--

    public List<SubCommand> getSubcommands(){
        return subcommands;
    }
}
