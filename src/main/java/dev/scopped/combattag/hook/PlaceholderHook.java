package dev.scopped.combattag.hook;

import dev.scopped.combattag.CombatTagPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderHook extends PlaceholderExpansion {

    private final CombatTagPlugin plugin;

    public PlaceholderHook(CombatTagPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return switch (params) {
            case "time" -> String.format("%.1f", (double) plugin.combatService().timeLeft(player.getUniqueId()) / 1000);
            case "tagged" -> "" + plugin.combatService().isTagged(player);
            default -> "Placeholder not found";
        };
    }

    @Override
    public @NotNull String getIdentifier() {
        return "combat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Scopped_";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
}
