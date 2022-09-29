package com.daviddevelops.DeathBackup;

import com.daviddevelops.DeathBackup.Utility.Utility;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathbackupPlugin extends JavaPlugin {

    Utility utility;

    public void onEnable(){
        utility = new Utility(this);
        getServer().getPluginManager().registerEvents(utility.getEventHandler(), this);
    }

    public void onDisable(){

    }

}
