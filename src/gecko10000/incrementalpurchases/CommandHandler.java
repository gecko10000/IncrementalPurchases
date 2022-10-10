package gecko10000.incrementalpurchases;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;

public class CommandHandler {

    private final MiniMessage miniMessage;

    public CommandHandler() {
        this.miniMessage = MiniMessage.miniMessage();
        new CommandParser(IncrementalPurchases.get().getResource("command.rdcml"))
                .setArgTypes(new ArgType<>("incrementalpurchase", s -> Config.purchases.get(s))
                        .tabStream(s -> Config.purchases.keySet().stream()))
                .parse().register("incrementalpurchases", this);
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        IncrementalPurchases.get().reload();
        sender.sendMessage(miniMessage.deserialize(Lang.configsReloaded));
    }

    @CommandHook("buy")
    public void buy(CommandSender sender, Player target, IncrementalPurchase purchase, boolean silent) {
        IncrementalPurchase.Response response = purchase.buy(target);
        if (silent) return;
        if (response == IncrementalPurchase.Response.SUCCESS) {
            target.sendMessage(miniMessage.deserialize(Lang.successfulBuy, Placeholder.unparsed("amount", "" + response.newValue)));
        } else if (response == IncrementalPurchase.Response.NOT_ENOUGH_MONEY) {
            target.sendMessage(miniMessage.deserialize(Lang.notEnoughMoney, Placeholder.unparsed("amount", Utils.formatMoney(purchase.getPrice(target)))));
        } else {
            target.sendMessage(miniMessage.deserialize(Lang.permissionMaxed));
        }
    }

    @CommandHook("reset")
    public void reset(CommandSender sender, Player target, IncrementalPurchase purchase) {
        purchase.resetStoredPurchases(target);
        sender.sendMessage(miniMessage.deserialize(Lang.resetPurchases, Placeholder.unparsed("player", target.getName())));
    }

    @CommandHook("increment")
    public void increment(CommandSender sender, Player target, IncrementalPurchase purchase, int amount) {
        purchase.incrementPermission(target, amount);
        sender.sendMessage(miniMessage.deserialize(Lang.incrementedPurchase, Placeholder.unparsed("player", target.getName()), Placeholder.unparsed("amount", "" + amount)));
    }

}
