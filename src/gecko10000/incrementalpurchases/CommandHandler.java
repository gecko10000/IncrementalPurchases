package gecko10000.incrementalpurchases;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;

public class CommandHandler {

    public CommandHandler() {
        new CommandParser(IncrementalPurchases.get().getResource("command.rdcml"))
                .parse().register("incrementalpurchases", this);
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        IncrementalPurchases.get().reload();
        sender.sendMessage(Component.text("Config reloaded!", NamedTextColor.GREEN));
    }

}
