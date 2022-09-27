package gecko10000.incrementalpurchases;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.config.annotations.ConfigPath;

import java.util.HashMap;
import java.util.Map;

@ConfigMappable
public class IncrementalPurchase {

    @ConfigPath
    private String key;
    private double startPrice;
    private double multiplier;
    private Operator operator;

    private final transient Map<Integer, Double> priceCache = new HashMap<>();
    private transient int cacheCalculated = -1;

    // for config manager
    private IncrementalPurchase() {}

    public IncrementalPurchase(String key, double startPrice, double multiplier, Operator operator) {
        this.key = key;
        this.startPrice = startPrice;
        this.multiplier = multiplier;
        this.operator = operator;
    }

    public boolean buy(Player player) {
        double price = getPrice(player);
        if (!IncrementalPurchases.get().economy.withdrawPlayer(player, price).transactionSuccess()) {
            return false;
        }
        setStoredPurchases(player, getStoredPurchases(player) + 1);
        // TODO: permission increment
        return true;
    }

    public double getPrice(Player player) {
        int alreadyPurchased = getStoredPurchases(player);
        return getPrice(alreadyPurchased);
    }

    private double getPrice(int alreadyPurchased) {
        if (cacheCalculated >= alreadyPurchased) return priceCache.get(alreadyPurchased);
        return cache(alreadyPurchased);
    }

    private double cache(int purchases) {
        double price = priceCache.getOrDefault(cacheCalculated, startPrice);
        for (int i = cacheCalculated + 1; i <= purchases; i++) {
            price = switch (operator) {
                case ADD -> price + multiplier;
                case MULTIPLY -> price * multiplier;
                case POWER -> Math.pow(price, multiplier);
            };
            priceCache.put(i, price);
        }
        cacheCalculated = purchases;
        return price;
    }

    private int getStoredPurchases(Player player) {
        return player.getPersistentDataContainer().getOrDefault(new NamespacedKey(IncrementalPurchases.get(), key), PersistentDataType.INTEGER, 0);
    }

    private void setStoredPurchases(Player player, int amount) {
        player.getPersistentDataContainer().set(new NamespacedKey(IncrementalPurchases.get(), key), PersistentDataType.INTEGER, amount);
    }

}
