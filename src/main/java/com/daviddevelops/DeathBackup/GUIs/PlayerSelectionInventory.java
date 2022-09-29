package com.daviddevelops.DeathBackup.GUIs;

import com.daviddevelops.DeathBackup.Utility.Utility;
import com.daviddevelops.DeathBackup.InventoryAPI.ClickableItem;
import com.daviddevelops.DeathBackup.InventoryAPI.SmartInventory;
import com.daviddevelops.DeathBackup.InventoryAPI.content.InventoryContents;
import com.daviddevelops.DeathBackup.InventoryAPI.content.InventoryProvider;
import com.daviddevelops.DeathBackup.InventoryAPI.content.Pagination;
import com.daviddevelops.DeathBackup.InventoryAPI.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerSelectionInventory implements InventoryProvider {

    Utility ut = Utility.getInstance();

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("selectioninventory")
            .provider(new PlayerSelectionInventory())
            .size(5, 9)
            .title("Select a user!")
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        PlayerDeathInventory inv;
        ClickableItem blackGlass = ut.registerItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ");
        inventoryContents.fillBorders(blackGlass);
        Pagination pagination = inventoryContents.pagination();
        ClickableItem[] items = new ClickableItem[Bukkit.getServer().getOnlinePlayers().size()];
        int i = 0;
        for(Player p : Bukkit.getOnlinePlayers()) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullM = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
            skullM.setOwner(p.getName());
            skull.setItemMeta(skullM);
            PlayerDeathInventory PlayerDeathInventory;
            items[i] = ClickableItem.of(ut.recolorItem(skull, "&f" + p.getName()),
                    e -> new PlayerDeathInventory().setPlayer(p.getName(),player));
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
