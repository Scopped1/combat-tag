package dev.scopped.combattag.commands;

import dev.scopped.combattag.CombatTagPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CombatReloadCommand implements CommandExecutor {

    private final CombatTagPlugin plugin;

    public CombatReloadCommand(CombatTagPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player) || !player.hasPermission("combat.admin")) return false;

        plugin.reload();
        plugin.send(player, plugin.config().getString("messages.config_reloaded"));

        return true;
    }
}
