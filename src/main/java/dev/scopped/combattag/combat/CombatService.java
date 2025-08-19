package dev.scopped.combattag.combat;

import dev.scopped.combattag.CombatTagPlugin;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatService {

    private final Map<UUID, Long> combatPlayers = new ConcurrentHashMap<>();
    private final CombatTagPlugin plugin;

    public CombatService(CombatTagPlugin plugin) {
        this.plugin = plugin;
    }

    public void tagPlayer(Player player, Player damager) {
        if (player == null || player.equals(damager)) return;
        long combatTime = plugin.config().getInt("combat_time", 15) * 1000L;

        if (!isTagged(player)) {
            plugin.send(player, plugin.config().getString("messages.enter_combat"));
        }
        updateTagTime(player, combatTime + System.currentTimeMillis());

        if (damager == null) return;

        if (!isTagged(damager)) {
            plugin.send(damager, plugin.config().getString("messages.enter_combat"));
        }
        updateTagTime(damager, combatTime + System.currentTimeMillis());
    }

    public void untagPlayer(Player player) {
        combatPlayers.remove(player.getUniqueId());
        plugin.send(player, plugin.config().getString("messages.exit_combat"));
    }

    public void updateTagTime(Player player, long tagTime) {
        combatPlayers.put(player.getUniqueId(), tagTime);
    }

    public long timeLeft(UUID uuid) {
        return Math.max(0, combatPlayers.getOrDefault(uuid, 0L) - System.currentTimeMillis());
    }

    public boolean isTagged(Player player) {
        return combatPlayers.containsKey(player.getUniqueId()) && combatPlayers.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    public void init() {
        plugin.server().getScheduler().runTaskTimerAsynchronously(plugin.bootstrap(), () -> {
            for (Player onlinePlayer : plugin.server().getOnlinePlayers()) {
                long remainingTime = timeLeft(onlinePlayer.getUniqueId());

                if (remainingTime <= 0 && combatPlayers.containsKey(onlinePlayer.getUniqueId())) {
                    untagPlayer(onlinePlayer);
                    continue;
                }

                if (remainingTime > 0) {
                    String formattedTime = String.format("%.1f", (double) remainingTime / 1000);
                    plugin.actionBar(onlinePlayer, plugin.config().getString("messages.action_bar"), "{time}", formattedTime);
                }
            }
        }, 0, 2L);
    }

    public void stop() {
        combatPlayers.clear();
    }

    public Map<UUID, Long> combatPlayers() {
        return combatPlayers;
    }
}
