package gecko10000.incrementalpurchases;

import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.config.annotations.ConfigMappable;

import java.util.ArrayList;
import java.util.List;

@ConfigMappable
public class IncrementalPurchases extends JavaPlugin {

    private static IncrementalPurchases plugin;
    public List<IncrementalPurchase> purchases = new ArrayList<>();

    public void onEnable() {
        plugin = this;
        reload();
        new CommandHandler();
    }

    public void reload() {
        ConfigManager.create(this)
                .target(this).saveDefaults().load();
    }

    public static IncrementalPurchases get() {
        return plugin;
    }

}
