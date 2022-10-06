package gecko10000.incrementalpurchases;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class PAPIExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "ip";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", IncrementalPurchases.get().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return IncrementalPurchases.get().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return null;
        final String priceString = "_price";
        final String priceCleanString = "_price_clean";
        final String currentPermission = "_current_permission";
        final String nextPermission = "_next_permission";
        final String max = "_max";
        if (params.endsWith(priceString)) {
            Double price = get(player, params.substring(0, params.length() - priceString.length()), i -> i.getPrice(player));
            return price == null ? null : Utils.formatMoney(price);
        }
        if (params.endsWith(priceCleanString)) {
            Double price = get(player, params.substring(0, params.length() - priceCleanString.length()), i -> i.getPrice(player));
            return price + "";
        }
        if (params.endsWith(currentPermission)) {
            int highest = get(player, params.substring(0, params.length() - currentPermission.length()), i -> i.getHighestPermission(player));
            return String.valueOf(highest);
        }
        if (params.endsWith(nextPermission)) {
            return "" + get(player, params.substring(0, params.length() - nextPermission.length()), i -> i.getNextPermission(player));
        }
        if (params.endsWith(max)) {
            return "" + get(player, params.substring(0, params.length() - max.length()), IncrementalPurchase::getMaxPermission);
        }
        return null;
    }

    private <T> T get(Player player, String purchaseName, Function<IncrementalPurchase, T> function) {
        return Optional.ofNullable(Config.purchases.get(purchaseName)).map(function).orElse(null);
    }

}
