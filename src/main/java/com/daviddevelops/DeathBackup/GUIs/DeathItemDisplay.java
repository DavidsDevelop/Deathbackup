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
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathItemDisplay implements InventoryProvider {

    Utility ut = Utility.getInstance();
    Player rewardedPlayer;
    String ID;

    public void setPlayer(String name, String ID ,Player player){
        this.rewardedPlayer = Bukkit.getPlayer(name);
        this.ID = ID;
        INVENTORY.open(player);
    }
    public final SmartInventory INVENTORY = SmartInventory.builder()
            .id("deathiteminventory")
            .provider(this)
            .size(5, 9)
            .title("Reviewing items...")
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        ClickableItem blackGlass = ut.registerItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ");
        inventoryContents.fillBorders(blackGlass);
        Pagination pagination = inventoryContents.pagination();

        ConfigurationSection CS = ConfigHandler.getInstance().getConfig("playerData.yml").getConfigurationSection(player.getName());

        List<ItemStack> inv = Utility.getInstance().inventoryFromConfig(ID, rewardedPlayer);

        ClickableItem[] items = new ClickableItem[inv.size()];

        int i = 0;
        for(ItemStack item : inv) {
            if(item != null){
                items[i] = ut.registerItem(item, "&f" + item.getItemMeta().getDisplayName());
                i++;
            }
        }


        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        inventoryContents.set(4, 3, ClickableItem.of(ut.recolorItem(new ItemStack(Material.ARROW), "&fBack"),
                e -> INVENTORY.open(player, pagination.previous().getPage())));
        inventoryContents.set(4, 5, ClickableItem.of(ut.recolorItem(new ItemStack(Material.ARROW), "&fNext"),
                e -> INVENTORY.open(player, pagination.next().getPage())));

        inventoryContents.set(4, 0, ClickableItem.of(ut.recolorItem(new ItemStack(Material.REDSTONE_TORCH), "&fExit"),
                e -> backButton(player)));
        inventoryContents.set(4, 8, ClickableItem.of(ut.recolorItem(new ItemStack(Material.GREEN_WOOL), "&fReward"),
                e -> rewardPlayer(player, inv)));

    }

    public void rewardPlayer(Player player, List<ItemStack> inv){
        FileConfiguration FC = ConfigHandler.getInstance().getConfig("playerData.yml");
        System.out.println(ConfigHandler.getInstance().getStringRaw(FC,rewardedPlayer.getName() + "." + ID));
        ConfigHandler.getInstance().setData(FC, rewardedPlayer.getName() + "." + ID, null);
        for(ItemStack item : inv){
            if(item != null){
                rewardedPlayer.getInventory().addItem(item);
            }
        }
        backButton(player);
    }

    public void backButton(Player player){
        new PlayerDeathInventory().setPlayer(rewardedPlayer.getName(), player);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
