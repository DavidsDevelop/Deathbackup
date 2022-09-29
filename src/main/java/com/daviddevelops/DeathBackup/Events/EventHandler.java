package com.daviddevelops.DeathBackup.Events;

import com.daviddevelops.DeathBackup.GUIs.PlayerSelectionInventory;
import com.daviddevelops.DeathBackup.Utility.ConfigHandler;
import com.daviddevelops.DeathBackup.Utility.Utility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Utility.inventoryToConfig(event.getEntity().getInventory(), event.getEntity().getLastDamageCause(), event.getEntity().getLastDeathLocation());
    }

}
