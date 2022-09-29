package com.daviddevelops.DeathBackup;

import com.daviddevelops.DeathBackup.InventoryAPI.InventoryManager;
import com.daviddevelops.DeathBackup.Utility.ConfigHandler;
import com.daviddevelops.DeathBackup.Utility.Utility;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathbackupPlugin extends JavaPlugin {

    Utility utility;
    private static DeathbackupPlugin instance;
    private static InventoryManager invManager;


    public void onEnable(){
        utility = new Utility(this);
        ConfigHandler.getInstance().setPlugin(this);
        getServer().getPluginManager().registerEvents(utility.getEventHandler(), this);
        getCommand("adbp").setExecutor(utility.getCommandHandler());

        instance = this;

        invManager = new InventoryManager(this);
        invManager.init();
    }

    public void onDisable(){

    }

    public static InventoryManager manager() { return invManager; }
    public static DeathbackupPlugin instance() { return instance; }

}
