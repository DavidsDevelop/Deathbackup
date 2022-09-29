package com.daviddevelops.DeathBackup.GUIs;

import com.daviddevelops.DeathBackup.Utility.ConfigHandler;
import com.daviddevelops.DeathBackup.Utility.Utility;
import com.daviddevelops.DeathBackup.InventoryAPI.ClickableItem;
import com.daviddevelops.DeathBackup.InventoryAPI.SmartInventory;
import com.daviddevelops.DeathBackup.InventoryAPI.content.InventoryContents;
import com.daviddevelops.DeathBackup.InventoryAPI.content.InventoryProvider;
import com.daviddevelops.DeathBackup.InventoryAPI.content.Pagination;
import com.daviddevelops.DeathBackup.InventoryAPI.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeathInventory implements InventoryProvider {

    Utility ut = Utility.getInstance();
    String rewardedPlayer;

    public boolean setPlayer(String name, Player player){
        rewardedPlayer = Bukkit.getServer().getPlayer(name).getName();
        if(rewardedPlayer == null){
            return false;
        }
        INVENTORY.open(player);
        return true;
    }
    public final SmartInventory INVENTORY = SmartInventory.builder()
            .id("deathinventory")
            .provider(this)
            .size(5, 9)
            .title("Select Inventory!")
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        ClickableItem blackGlass = ut.registerItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ");
        inventoryContents.fillBorders(blackGlass);
        Pagination pagination = inventoryContents.pagination();

        ConfigurationSection CS = ConfigHandler.getInstance().getConfig("playerData.yml").getConfigurationSection(rewardedPlayer);

        if(CS == null){
            return;
        }

        ClickableItem[] items = new ClickableItem[CS.getKeys(false).size()];

        int i = 0;
        for(String key : CS.getKeys(false)) {
            ItemStack box = new ItemStack(Material.SHULKER_BOX);
            ItemMeta meta = box.getItemMeta();
            List<String> lore = new ArrayList<>();
            FileConfiguration FC = ConfigHandler.getInstance().getConfig("playerData.yml");

            lore.add("&6Time: " + ConfigHandler.getInstance().getStringRaw(FC, rewardedPlayer + "." + key + ".Time"));
            lore.add("&6Death reason: " + ConfigHandler.getInstance().getStringRaw(FC, rewardedPlayer + "." + key + ".Cause"));
            Location loc = ConfigHandler.getInstance().getLocation(FC, rewardedPlayer + "." + key + ".Location");
            lore.add("&6World: " + loc.getWorld().getName());
            lore.add("&6X: " + loc.getX());
            lore.add("&6Y: " + loc.getY());
            lore.add("&6Z: " + loc.getZ());
            items[i] = ClickableItem.of(ut.recolorItem(box, "&f" + key, lore), e -> new DeathItemDisplay().setPlayer(rewardedPlayer, key, player));
            i++;
        }


        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        inventoryContents.set(4, 3, ClickableItem.of(ut.recolorItem(new ItemStack(Material.ARROW), "&fBack"),
                e -> INVENTORY.open(player, pagination.previous().getPage())));
        inventoryContents.set(4, 5, ClickableItem.of(ut.recolorItem(new ItemStack(Material.ARROW), "&fNext"),
                e -> INVENTORY.open(player, pagination.next().getPage())));

    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
