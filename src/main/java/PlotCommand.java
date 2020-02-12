package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotCommand implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public PlotCommand(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    private String buyPlot(String owner_uuid, int pos_x, int pos_z, int size, boolean operator) {
        if (this.plugin.db.checkColliding(pos_x, pos_z, size)) {
            return ChatColor.GRAY + "Here is already somebody building.";
        }

        Plot new_plot = new Plot(size, pos_x, pos_z, true);
        com.einspaten.bukkit.mcpillage.PluginPlayer plugin_player = this.plugin.getPlayer(owner_uuid);
        plugin_player.add_plot(new_plot);
        this.plugin.db.addPlot(new_plot, owner_uuid, true);

        int price = (int) (Math.log(size * size) * 100);
        plugin_player.increaseMoney(-1 * price);
        this.plugin.db.setMoney(owner_uuid, -1 * price);
        // Todo: Charge Price
        return ChatColor.GRAY + "Successfully bought plot for " + ChatColor.GREEN + " $";
    }


    private String addPlot(String owner_uuid, String other_user, int pos_x, int pos_z, boolean operator) {

        com.einspaten.bukkit.mcpillage.PluginPlayer plugin_player = this.plugin.getPlayer(owner_uuid);

        Plot this_plot = plugin_player.getPlot(pos_x, pos_z);

        if (this_plot == null) {
            return ChatColor.GRAY + "You are not staying on one of your plots";
        }
        if (!(this_plot.getOwner() || operator)) {
            return ChatColor.GRAY + "You don't have the permissions to do that.";
        }

        String other_user_uuid = this.plugin.db.resolveUsername(other_user);

        if (other_user_uuid == null) {
            return ChatColor.GRAY + "This user does not exist on this Server";
        }

        com.einspaten.bukkit.mcpillage.PluginPlayer other_plugin_player = this.plugin.getPlayer(other_user_uuid);

        if (other_plugin_player != null) {
            other_plugin_player.add_plot(new Plot(this_plot.getSize(), this_plot.getPos_x(), this_plot.getPos_z(), false));
        }

        this.plugin.db.addPlot(this_plot, other_user_uuid, false);

        return ChatColor.GREEN + "Successfully added player to plot";
    }

    private String removePlot(String owner_uuid, String other_user, int pos_x, int pos_z, boolean operator) {
        com.einspaten.bukkit.mcpillage.PluginPlayer plugin_player = this.plugin.getPlayer(owner_uuid);
        Plot this_plot = plugin_player.getPlot(pos_x, pos_z);

        if (this_plot == null) {
            return ChatColor.GRAY + "You are not staying on one of your plots";
        }
        if (!(this_plot.getOwner() || operator)) {
            return ChatColor.GRAY + "You don't have the permissions to do that.";
        }

        String other_user_uuid = this.plugin.db.resolveUsername(other_user);

        if (other_user_uuid == null) {
            return ChatColor.GRAY + "This user does not exist on this Server";
        }

        com.einspaten.bukkit.mcpillage.PluginPlayer other_plugin_player = this.plugin.getPlayer(other_user_uuid);

        if (other_plugin_player != null) {
            other_plugin_player.remove_plot(this_plot);
        }

        this.plugin.db.removePlot(other_user_uuid, this_plot);

        return ChatColor.GREEN + "Successfully removed player from your plot";
    }

    private String sellPlot(String owner_uuid, int pos_x, int pos_z, boolean operator) {
        com.einspaten.bukkit.mcpillage.PluginPlayer plugin_player = this.plugin.getPlayer(owner_uuid);
        Plot this_plot = plugin_player.getPlot(pos_x, pos_z);
        Bukkit.broadcastMessage("Owner" + this_plot.getOwner() + "op:" + operator);
        if (this_plot == null) {
            return ChatColor.GRAY + "You are not staying on one of your plots";
        }
        if (!(this_plot.getOwner() || operator)) {
            return ChatColor.GRAY + "You don't have the permissions to do that.";
        }

        this.plugin.db.sellPlot(this_plot);

        PluginPlayer[] all_players = this.plugin.getAllPlayers();

        for (PluginPlayer pplayer : all_players) {
            pplayer.remove_plot(this_plot);
        }
        return ChatColor.GREEN + "Successfully sold plot";
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Player player = (Player) sender;

        if (split[0].equalsIgnoreCase("buy")) {
            if (split.length < 4) {
                player.sendMessage(ChatColor.GRAY + "This Function needs size, pos_x and pos_z of the Plot you want to buy");
                return true;
            }
            int size, pos_x, pos_z;

            try {
                size = Integer.parseInt(split[1]);
                pos_x = Integer.parseInt(split[2]);
                pos_z = Integer.parseInt(split[3]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.GRAY + "Invalid input size(Radius), pos_x and pos_y should be numbers");
                return true;
            }

            if (size < 15 || size > 125) {
                player.sendMessage(ChatColor.GRAY + "The Plot size can vary between 30 and 250 blocks");
                return true;
            }

            player.sendMessage(buyPlot(player.getUniqueId().toString(), pos_x, pos_z, size, false));

        } else if (split[0].equalsIgnoreCase("add")) {
            if (split.length < 2) {
                player.sendMessage(ChatColor.GRAY + "/city add [username] while staying on the plot you want the user to be added");
                return true;
            }
            String username = split[1];
            Location loc = player.getLocation();
            String response = addPlot(player.getUniqueId().toString(), username, (int) loc.getX(), (int) loc.getZ(), false);
            player.sendMessage(response);

        } else if (split[0].equalsIgnoreCase("remove")) {
            if (split.length < 2) {
                player.sendMessage(ChatColor.GRAY + "/city remove [username] while staying on the plot you want the user to be added");
                return true;
            }

            String username = split[1];
            Location loc = player.getLocation();
            player.sendMessage(removePlot(player.getUniqueId().toString(), username, (int) loc.getX(), (int) loc.getZ(), false));
        } else if (split[0].equalsIgnoreCase("force_buy") && player.isOp()) { // Operator variants of existing commands
            if (split.length < 4) {
                player.sendMessage(ChatColor.GRAY + "This Function needs size, pos_x and pos_z of the Plot you want to buy");
                return true;
            }
            int size, pos_x, pos_z;

            try {
                size = Integer.parseInt(split[1]);
                pos_x = Integer.parseInt(split[2]);
                pos_z = Integer.parseInt(split[3]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.GRAY + "Invalid input size(Radius), pos_x and pos_y should be numbers");
                return true;
            }

            player.sendMessage(buyPlot(player.getUniqueId().toString(), pos_x, pos_z, size, true));

        } else if (split[0].equalsIgnoreCase("sell")) {
            player.sendMessage(sellPlot(player.getUniqueId().toString(), (int) player.getLocation().getX(), (int) player.getLocation().getZ(), false));

        } else if (split[0].equalsIgnoreCase("price")) {
            if (split.length < 2) {
                player.sendMessage(ChatColor.GRAY + "This Function needs your plot size to calculate the price");
                return true;
            }
            int size;

            try {
                size = Integer.parseInt(split[1]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.GRAY + "Invalid input size - should be numbers");
                return true;
            }
            int price = (int) (Math.log(size * size) * 100);

            player.sendMessage(ChatColor.GRAY + "Price of the plot will be " + ChatColor.GREEN + price + "$");


        } else if (split[0].equalsIgnoreCase("force_add") && player.isOp()) {
            if (split.length < 2) {
                player.sendMessage(ChatColor.GRAY + "/city add [username] while staying on the plot you want the user to be added");
                return true;
            }
            String username = split[1];
            Location loc = player.getLocation();
            String response = addPlot(player.getUniqueId().toString(), username, (int) loc.getX(), (int) loc.getZ(), true);
            player.sendMessage(response);

        } else if (split[0].equalsIgnoreCase("force_remove") && player.isOp()) {
            if (split.length < 2) {
                player.sendMessage(ChatColor.GRAY + "/city remove [username] while staying on the plot you want the user to be added");
                return true;
            }
            String username = split[1];
            Location loc = player.getLocation();
            player.sendMessage(removePlot(player.getUniqueId().toString(), username, (int) loc.getX(), (int) loc.getZ(), true));
        } else if (split[0].equalsIgnoreCase("force_sell") && player.isOp()) {
            Location loc = player.getLocation();
            player.sendMessage(sellPlot(player.getUniqueId().toString(), (int) loc.getX(), (int) loc.getZ(), true));
        } else {
            String help_page = ChatColor.GRAY + "Plot Help Page \n"
                    + " * /plot buy [size] [pos_x] [pos_z] \n"
                    + " * /plot add [username] \n"
                    + " * /plot remove [username] \n"
                    + " * /plot sell\n"
                    + " * /plot price [size]";
            player.sendMessage(help_page);
        }
        return true;
    }
}
