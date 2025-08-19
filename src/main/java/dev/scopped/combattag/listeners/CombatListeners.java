package dev.scopped.combattag.listeners;

import dev.scopped.combattag.CombatTagPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class CombatListeners implements Listener {

    private final CombatTagPlugin plugin;

    public CombatListeners(CombatTagPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        Entity damager = event.getDamager();

        if (damager instanceof Projectile projectile) damager = (Entity) projectile.getShooter();
        if (!(damager instanceof Player attacker)) return;

        if (player.equals(attacker)) {
            plugin.combatService().tagPlayer(player, null);
        } else {
            plugin.combatService().tagPlayer(player, attacker);
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.combatService().isTagged(player)) {
            player.setHealth(0);
            player.spigot().respawn();

            plugin.combatService().untagPlayer(player);
            plugin.broadcast(plugin.config().getString("messages.quit_player_death"), "{player}", player.getName());
        }
    }

    @EventHandler
    public void commandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        String command = event.getMessage().split(" ")[0].toLowerCase();

        if (!plugin.combatService().isTagged(player) || player.hasPermission("combat.bypass")) return;

        List<String> allowedCommands = plugin.config().getStringList("settings.allowed_commands");
        if (!allowedCommands.contains(command)) {
            plugin.send(player, plugin.config().getString("messages.cant_execute_command"));
            event.setCancelled(true);
        }
    }
}
