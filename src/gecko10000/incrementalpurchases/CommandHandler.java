package gecko10000.incrementalpurchases;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;

public class CommandHandler {

    public CommandHandler() {
        new CommandParser(IncrementalPurchases.get().getResource("command.rdcml"))
                .parse().register("whatdoesthisdo", this);
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        IncrementalPurchases.get().reload();
        IncrementalPurchases.get().purchases.add(new IncrementalPurchase("testing", 50000, 1.012, Operator.MULTIPLY));
    }

    @CommandHook("test")
    public void test(Player player) {
        System.out.println(IncrementalPurchases.get().purchases.get(0).getPrice(player));
    }

}
