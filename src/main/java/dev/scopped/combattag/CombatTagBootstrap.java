package dev.scopped.combattag;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class CombatTagBootstrap extends JavaPlugin {

    private CombatTagPlugin plugin;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            this.plugin = new CombatTagPlugin(this);
        } catch (Exception e) {
            getServer().getPluginManager().disablePlugin(this);
            getLogger().log(Level.SEVERE, "Failed to load plugin", e);
        }
    }

    @Override
    public void onDisable() {
        if (this.plugin != null) this.plugin.disable();
    }
}
