package gecko10000.incrementalpurchases;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
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
    private String permissionPrefix;
    private int maxPermission = Integer.MAX_VALUE;
    private int permissionInterval;

    private final transient Map<Integer, Double> priceCache = new HashMap<>();
    private transient int cacheCalculated = -1;

    // for config manager
    private IncrementalPurchase() {}

    public IncrementalPurchase(String key, double startPrice, double multiplier, Operator operator, String permissionPrefix, int max) {
        this(key, startPrice, multiplier, operator, permissionPrefix, max, 1);
    }

    public IncrementalPurchase(String key, double startPrice, double multiplier, Operator operator, String permissionPrefix, int max, int permissionInterval) {
        this.key = key;
        this.startPrice = startPrice;
        this.multiplier = multiplier;
        this.operator = operator;
        this.permissionPrefix = permissionPrefix;
        this.maxPermission = max;
        this.permissionInterval = permissionInterval;
    }

    enum Response {
        SUCCESS,
        NOT_ENOUGH_MONEY,
        MAX;

        int newValue;
    }

    public Response buy(Player player) {
        int current = getHighestPermission(player);
        if (current >= maxPermission) {
            return Response.MAX;
        }
        double price = getPrice(player);
        if (!IncrementalPurchases.get().getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
            return Response.NOT_ENOUGH_MONEY;
        }
        setStoredPurchases(player, getStoredPurchases(player) + 1);
        Response r = Response.SUCCESS;
        r.newValue = incrementPermission(player);
        return Response.SUCCESS;
    }

    private int incrementPermission(Player player) {
        int highestExisting = getHighestPermission(player);
        int newValue = getNextPermission(player);
        runCommand(player, Config.setCommand, newValue);
        if (highestExisting != 0) {
            runCommand(player, Config.unsetCommand, highestExisting);
        }
        return newValue;
    }

    public int getNextPermission(Player player) {
        int highestExisting = getHighestPermission(player);
        return Math.min(maxPermission, highestExisting + permissionInterval);
    }

    private void runCommand(Player player, String command, int amount) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                command.replaceAll("<player>", player.getName())
                        .replaceAll("<permission>", permissionPrefix + amount));
    }

    public int getHighestPermission(Player player) {
        int highest = 0;
        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (!attachmentInfo.getValue()) continue;
            String permission = attachmentInfo.getPermission();
            if (!permission.startsWith(permissionPrefix)) continue;
            String supposedAmount = permission.substring(permissionPrefix.length());
            Integer amount = tryParseInt(supposedAmount);
            if (amount == null) continue;
            highest = Math.max(highest, amount);
        }
        return highest;
    }

    public int getMaxPermission() {
        return maxPermission;
    }

    private Integer tryParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
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

    public void resetStoredPurchases(Player player) {
        setStoredPurchases(player, 0);
    }

    private int getStoredPurchases(Player player) {
        return player.getPersistentDataContainer().getOrDefault(new NamespacedKey(IncrementalPurchases.get(), key), PersistentDataType.INTEGER, 0);
    }

    private void setStoredPurchases(Player player, int amount) {
        player.getPersistentDataContainer().set(new NamespacedKey(IncrementalPurchases.get(), key), PersistentDataType.INTEGER, amount);
    }

}
