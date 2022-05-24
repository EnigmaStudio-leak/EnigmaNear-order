package me.jamesroll.enigmastudio.near.command;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.jamesroll.enigmastudio.near.NearPlugin;
import me.jamesroll.enigmastudio.near.command.api.JamesCommand;
import me.jamesroll.enigmastudio.near.utility.SideType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NearCommand extends JamesCommand<CommandSender> {
    public NearCommand() {
        super("near", new String[] { "playersnear", "nearbyplayers", "jamesnear" });
    }

    public void handleExecute(CommandSender commandSender, String[] args) {
        Player player = Bukkit.getPlayer(commandSender.getName());
        if (commandSender instanceof org.bukkit.command.ConsoleCommandSender || player == null) {
            commandSender.sendMessage(NearPlugin.getInstance().getMessage("Messages.OnlyPlayersMessage"));
            return;
        }
        if(player.hasPermission(NearPlugin.getInstance().getMessage("Settings.Permission"))) {
            HashBasedTable<Player, Integer, SideType> hashBasedTable = HashBasedTable.create();
            Bukkit.getOnlinePlayers().forEach(s -> {
                double distance = s.getLocation().distance(player.getLocation());
                if (distance <= NearPlugin.getInstance().getDouble("NearRange") && !s.getName().equals(player.getName())) {
                    int n = (int) Math.ceil(s.getLocation().distance(player.getLocation()));
                    hashBasedTable.put(s, n, SideType.of(player.getLocation().getDirection(), s.getLocation().getDirection()));
                }
            });
            player.sendMessage(NearPlugin.getInstance().getMessage("Messages.Header"));
            hashBasedTable.cellSet().forEach(c -> player.sendMessage(NearPlugin.getInstance().getMessage("Messages.PlayerFormat")
                    .replace("%player", NearPlugin.getInstance().getVault().getPrefix(((Player) c.getRowKey()).getName()) +
                            ((Player) c.getRowKey()).getName() + NearPlugin.getInstance().getVault().getSuffix(((Player) c.getRowKey()).getName()))
                    .replace("%distance", c.getColumnKey() + "")
                    .replace("%sign", ((SideType) c.getValue()).getSign()).replace("%side", ((SideType) c.getValue()).getRu())));
            player.sendMessage(NearPlugin.getInstance().getMessage("Messages.Footer"));
        }else{
            player.sendMessage(NearPlugin.getInstance().getMessage("Messages.NoPermission"));
        }
    }
}
