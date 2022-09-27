package gecko10000.incrementalpurchases;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.config.annotations.ConfigMappable;

import java.util.ArrayList;
import java.util.List;

@ConfigMappable
public class IncrementalPurchases extends JavaPlugin {

    private static IncrementalPurchases plugin;
    public List<IncrementalPurchase> purchases = new ArrayList<>();
    private Economy economy;

    public void onEnable() {
        plugin = this;
        setupEconomy();
        reload();
        new CommandHandler();
        purchases.add(new IncrementalPurchase("test", 3, 1.01, Operator.MULTIPLY));
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
                .target(this).saveDefaults().load();
    }

    public static IncrementalPurchases get() {
        return plugin;
    }

}
