package com.daviddevelops.DeathBackup.Utility;


import com.daviddevelops.DeathBackup.Commands.CommandHandler;
import com.daviddevelops.DeathBackup.Events.EventHandler;
import com.daviddevelops.DeathBackup.Player.PlayerHandler;
import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.daviddevelops.DeathBackup.InventoryAPI.ClickableItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utility {

    private static Utility single_inst = null;
    Plugin plugin; PlayerHandler playerHandler; CommandHandler commandHandler; EventHandler eventHandler;

    public Utility(Plugin plugin){
        single_inst = this;
        this.plugin = plugin;
        this.playerHandler = registerPlayerHandler(this);
        this.commandHandler = registerCommandHandler(this);
        this.eventHandler = registerEventHandler(this);
    }

//    --[ Instance Creation ]--

    public static Utility getInstance() {
        return single_inst;
    }

//    --[ Class command registration  ]--

    private EventHandler registerEventHandler(Utility utility) {
        return new EventHandler();
    }

    private CommandHandler registerCommandHandler(Utility utility) {
        return new CommandHandler(utility);
    }

    private PlayerHandler registerPlayerHandler(Utility utility) {
        return new PlayerHandler();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    //    --[ Math related Functions  ]--

    public double evaluateMath(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public ClickableItem registerItem(ItemStack itemStack, String s) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(translateColors(s));
        itemStack.setItemMeta(meta);
        return ClickableItem.empty(itemStack);
    }

//    --[ Item & Color Functions  ]--
    public ClickableItem registerItem(ItemStack itemStack, String s, List<String> l) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(translateColors(s));
        meta.setLore(translateColors(l));
        itemStack.setItemMeta(meta);
        return ClickableItem.empty(itemStack);
    }

    public ItemStack recolorItem(ItemStack itemStack, String s, List<String> l) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(translateColors(s));
        meta.setLore(translateColors(l));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack recolorItem(ItemStack itemStack, String s) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(translateColors(s));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public String translateColors(String s){
        return IridiumColorAPI.process(s);
    }

    private List<String> translateColors(List<String> s){
        List<String> newList = new ArrayList<>();
        for(String msg : s){
            newList.add(translateColors(msg));
        }
        return newList;
    }

//    --[ Inventory Functions  ]--
    public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = toBase64(playerInventory);
        String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

        return new String[] { content, armor };
    }
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public List<ItemStack> inventoryFromConfig(String ID, Player player){
        FileConfiguration FC = ConfigHandler.getInstance().getConfig("playerData.yml");
        List<String> test = (List<String>) ConfigHandler.getInstance().getList(FC, player.getName() + "." + ID + ".Inventory");
        List<ItemStack> inventory = new ArrayList<>();
        try {
            ItemStack items[] = Utility.itemStackArrayFromBase64(test.get(0));
            ItemStack armors[] = Utility.itemStackArrayFromBase64(test.get(1));
            inventory.addAll(Arrays.asList(items));
            inventory.addAll(Arrays.asList(armors));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        for(ItemStack item : inventory){
//            if(item != null){
//                player.getInventory().addItem(item);
//            }
//        }
        return inventory;
    }

    public static void inventoryToConfig(PlayerInventory inventory, EntityDamageEvent lastDamageCause, Location location){
        // Save to Config + Generate ID
        String base64[] = Utility.playerInventoryToBase64(inventory);
        UUID uuid = UUID.nameUUIDFromBytes(base64[0].getBytes());
        FileConfiguration FC = ConfigHandler.getInstance().getConfig("playerData.yml");
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        ConfigHandler.getInstance().setData(FC, inventory.getHolder().getName() + "." + uuid.toString() + ".Time", format.format(now).toString());
        ConfigHandler.getInstance().setData(FC, inventory.getHolder().getName() + "." + uuid.toString() + ".Cause", lastDamageCause.getEventName().toString());
        ConfigHandler.getInstance().addLocation(FC,  location, inventory.getHolder().getName() + "." + uuid.toString() + ".Location");
        ConfigHandler.getInstance().setData(FC, inventory.getHolder().getName() + "." + uuid.toString() + ".Inventory", base64);
    }
}
