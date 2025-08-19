package dev.scopped.combattag;

import dev.scopped.combattag.combat.CombatService;
import dev.scopped.combattag.commands.CombatReloadCommand;
import dev.scopped.combattag.hook.PlaceholderHook;
import dev.scopped.combattag.listeners.CombatListeners;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatTagPlugin {

    public static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private final CombatTagBootstrap bootstrap;
    private final BukkitAudiences adventure;

    private final CombatService combatService;

    public CombatTagPlugin(CombatTagBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.adventure = BukkitAudiences.create(bootstrap);

        this.combatService = new CombatService(this);
        combatService.init();

        server().getPluginManager().registerEvents(new CombatListeners(this), bootstrap);
        bootstrap.getCommand("combatreload").setExecutor(new CombatReloadCommand(this));

        new PlaceholderHook(this).register();
    }

    public void reload() {
        bootstrap.reloadConfig();
    }

    public void disable() {
        combatService.stop();
    }

    public Server server() {
        return bootstrap.getServer();
    }

    public FileConfiguration config() {
        return bootstrap.getConfig();
    }

    public void saveConfig() {
        bootstrap.saveConfig();
    }

    public JavaPlugin bootstrap() {
        return bootstrap;
    }

    public CombatService combatService() {
        return combatService;
    }

    public void send(Player player, String message, Object... params) {
        Audience audience = adventure().player(player);
        audience.sendMessage(LEGACY_COMPONENT_SERIALIZER.deserialize(replace(message, params)));
    }

    public void actionBar(Player player, String text, Object... params) {
        Audience audience = adventure().player(player);
        audience.sendActionBar(LEGACY_COMPONENT_SERIALIZER.deserialize(replace(text, params)));
    }

    public void broadcast(String message, Object... params) {
        for (Player player : server().getOnlinePlayers()) {
            send(player, message, params);
        }
    }

    public static String replace(String message, Object... params) {
        if (params.length % 2 != 0)
            throw new IllegalArgumentException("Parameters should be in key-value pairs.");

        for (int i = 0; i < params.length; i += 2)
            message = message.replace(params[i].toString(), params[i + 1].toString());

        return message;
    }

    public BukkitAudiences adventure() {
        return adventure;
    }
}
