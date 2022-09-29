package com.daviddevelops.DeathBackup.Events;

import com.daviddevelops.DeathBackup.Utility.Utility;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventHandler implements Listener {

    @org.bukkit.event.EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        PlayerInventory inventory = event.getEntity().getInventory();
        String base64[] = Utility.playerInventoryToBase64(inventory);
        try {
            Inventory inv = Utility.fromBase64(base64[0]);
            event.getEntity().getInventory().addItem(inv.getContents());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
