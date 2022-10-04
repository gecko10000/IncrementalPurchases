package gecko10000.incrementalpurchases;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.config.annotations.ConfigMappable;

@ConfigMappable
public class IncrementalPurchases extends JavaPlugin {

    private static IncrementalPurchases plugin;
    private Economy economy;

    public void onEnable() {
        plugin = this;
        setupEconomy();
        reload();
        new CommandHandler();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPIExpansion().register();
        }
    }

    public void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new RuntimeException("Economy plugin not found.");
        }
        economy = rsp.getProvider();
    }

    public Economy getEconomy() {
        return economy;
    }

    public void reload() {
        ConfigManager.create(this)
                .target(Config.class).saveDefaults().load();
        ConfigManager.create(this, "lang.yml")
                .target(Lang.class).saveDefaults().load();
    }

    public static IncrementalPurchases get() {
        return plugin;
    }

}
