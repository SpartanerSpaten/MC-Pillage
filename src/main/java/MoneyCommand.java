package com.einspaten.bukkit.mcpillage;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public MoneyCommand(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {

        Player player = (Player) sender;

        com.einspaten.bukkit.mcpillage.PluginPlayer plugin_player = this.plugin.getPlayer(player.getUniqueId().toString());

        if (split.length > 0) {
            if (split[0].equalsIgnoreCase("pay")) {
                if (split.length < 3) {
                    player.sendMessage("Help: /money pay [username] [amount]");
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(split[2]);
                } catch (NumberFormatException nfe) {
                    player.sendMessage("Invalid input Amount should be a valid number");
                    return true;
                }

                if (amount < 0) {
                    player.sendMessage("The amount should be positive number !");
                    return true;
                }
                if (plugin_player.getMoney() < amount) {
                    player.sendMessage("You don't have enough Money to perform this transaction");
                    return true;
                }

                String other_user_uuid = this.plugin.db.resolveUsername(split[1]);

                com.einspaten.bukkit.mcpillage.PluginPlayer other_plugin_player = this.plugin.getPlayer(other_user_uuid);

                if (other_plugin_player != null) {
                    // Is only called when other player is online
                    other_plugin_player.increaseMoney(amount);
                    other_plugin_player.sendMessage(ChatColor.GRAY + "You received a transaction " + ChatColor.GREEN + "$" + amount + ChatColor.GRAY + " worth from " + ChatColor.DARK_RED + player.getName());
                }

                plugin_player.increaseMoney(-1 * amount); // Can not be null

                this.plugin.db.setMoney(other_user_uuid, amount);
                this.plugin.db.setMoney(player.getUniqueId().toString(), -1 * amount);

                player.sendMessage(ChatColor.GRAY + "Gave " + ChatColor.DARK_RED + split[1] + " " + ChatColor.GREEN + "$" + amount);

            } else if (split[0].equalsIgnoreCase("generate") && player.isOp()) {

                if (split.length < 3) {
                    player.sendMessage("Help: /money generate [username] [amount] for operators only");
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(split[2]);
                } catch (NumberFormatException nfe) {
                    player.sendMessage("Invalid input Amount should be a valid number");
                    return true;
                }

                if (amount < 0) {
                    player.sendMessage("The amount should be positive number !");
                    return true;
                }

                String other_user_uuid = this.plugin.db.resolveUsername(split[1]);
                com.einspaten.bukkit.mcpillage.PluginPlayer other_plugin_player = this.plugin.getPlayer(other_user_uuid);

                if (other_plugin_player != null) {
                    // Is only called when other player is online
                    other_plugin_player.increaseMoney(amount);
                    other_plugin_player.sendMessage(ChatColor.GRAY + "You received a transaction " + ChatColor.GREEN + "$" + amount + ChatColor.GRAY + " worth from " + ChatColor.DARK_RED + player.getName());
                }

                this.plugin.db.setMoney(other_user_uuid, amount);

                player.sendMessage(ChatColor.GRAY + "Gave " + ChatColor.DARK_RED + split[1] + " " + ChatColor.GREEN + "$" + amount);

            } else {
                player.sendMessage(ChatColor.GRAY + "Commands");
                player.sendMessage(ChatColor.GRAY + " * /pay [username] [amount]");
            }
        }
        return true;
    }
}
