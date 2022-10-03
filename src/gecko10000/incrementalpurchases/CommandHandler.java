package gecko10000.incrementalpurchases;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.misc.FormatUtils;

public class CommandHandler {

    public CommandHandler() {
        new CommandParser(IncrementalPurchases.get().getResource("command.rdcml"))
                .setArgTypes(new ArgType<>("incrementalpurchase", s -> Config.purchases.get(s))
                        .tabStream(s -> Config.purchases.keySet().stream()))
                .parse().register("incrementalpurchases", this);
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        IncrementalPurchases.get().reload();
        sender.sendMessage(Component.text("Config reloaded!", NamedTextColor.GREEN));
    }

    @CommandHook("buy")
    public void buy(CommandSender sender, Player target, IncrementalPurchase purchase, boolean silent) {
        IncrementalPurchase.Response response = purchase.buy(target);
        if (silent) return;
        if (response == IncrementalPurchase.Response.SUCCESS) {
            target.sendMessage(Component.text("Permission increased to " + purchase.getHighestPermission(target) + ".", NamedTextColor.GREEN));
        } else if (response == IncrementalPurchase.Response.NOT_ENOUGH_MONEY) {
            target.sendMessage(Component.text("You don't have enough money! You need $" + FormatUtils.formatMoney(purchase.getPrice(target)) + ".", NamedTextColor.RED));
        } else {
            target.sendMessage(Component.text("This permission is already maxed!", NamedTextColor.RED));
        }
    }

    @CommandHook("reset")
    public void reset(CommandSender sender, Player target, IncrementalPurchase purchase) {
        purchase.resetStoredPurchases(target);
        sender.sendMessage(Component.text("Reset stored purchases.", NamedTextColor.GREEN));
    }

}
