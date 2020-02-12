package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.UUID;


public class PluginPlayer {
    private ArrayList<Plot> player_plots;

    private int home_x;
    private int home_y;
    private int home_z;
    private int home_dimension = 1; // See database updated when 1.16 drops
    private int money = 0;
    private int role = 0;

    private Player bukkit_player;
    private Team money_team;

    PluginPlayer(String uuid) {
        this.player_plots = new ArrayList<Plot>();
        money_team = null;
        bukkit_player = Bukkit.getPlayer(UUID.fromString(uuid));
    }

    String teleportHome() {

        if (home_y > 0) {
            World world;
            if (home_dimension == 1) {
                world = Bukkit.getServer().getWorld("world");
            } else {
                return "";
            }

            Location location = new Location(world, home_x, home_y, home_z);

            if (bukkit_player != null) {
                bukkit_player.teleport(location);
            }
            return ChatColor.GRAY + "Successfully Teleported";
        }
        return ChatColor.GRAY + "Pls set your home point first with /home set";
    }

    boolean onMyPlot(int pos_x, int pos_z) {
        for (Plot plot : player_plots) {
            if (plot.onHisPlot(pos_x, pos_z)) {
                return true;
            }
        }
        return false;
    }

    public Plot getPlot(int pos_x, int pos_z) {
        for (Plot plot : player_plots) {
            if (plot.onHisPlot(pos_x, pos_z)) {
                return plot;
            }
        }
        return null;
    }

    public void remove_plot(Plot removed_plot) {
        int iterator = 0;
        for (Plot plot : player_plots) {
            if (plot.getPos_x() == removed_plot.getPos_x() && plot.getPos_z() == removed_plot.getPos_z()) {
                player_plots.remove(iterator);
                return;
            }
            iterator++;
        }
    }

    public void increaseMoney(int amount) {
        money += amount;
        if (money_team != null) {
            money_team.setPrefix(ChatColor.GREEN + "$" + money);
        }
    }

    public void sendMessage(String message) {
        bukkit_player.sendMessage(message);
    }


    public void add_plot(Plot new_plot) {
        this.player_plots.add(new_plot);
    }

    public void setPlayer_plots(ArrayList<Plot> player_plots) {
        this.player_plots = player_plots;
    }

    public void setHome_x(int home_x) {
        this.home_x = home_x;
    }

    public void setHome_z(int home_z) {
        this.home_z = home_z;
    }

    public void setHome_y(int home_y) {
        this.home_y = home_y;
    }

    public void setMoney_team(Team money_team) {
        this.money_team = money_team;
    }

    public int getMoney() {
        return money;
    }

    public Player getBukkit_player() {
        return bukkit_player;
    }

    public void closeInventory() {
        this.bukkit_player.closeInventory();
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
