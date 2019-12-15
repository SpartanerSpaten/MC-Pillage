package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;


public class FactionCommand implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public FactionCommand(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }


    public void registerUser(OfflinePlayer target, int team) {

        target.setWhitelisted(true);
        Bukkit.getServer().reloadWhitelist();
        this.plugin.db.addToTeam(target.getName(), target.getUniqueId​().toString(), team);
        this.plugin.addPlayer(target.getUniqueId​().toString(), team);
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {

        Player player = (Player) sender;
        if (split.length > 0) {
            if (split[0].equalsIgnoreCase("spawn")) {
                // Only Trigger Teleport Event Teleport Handler will figure it out where Player should go
                World world = Bukkit.getServer().getWorld("world");
                Location location = new Location(world, 0, 100, 0);
                player.teleport(location);

            } else if (split[0].equalsIgnoreCase("members")) {


                int hisTeam = this.plugin.getMemberShip(player.getUniqueId​().toString());

                ArrayList<String> members = this.plugin.db.getMembersbyName(hisTeam);


                player.sendMessage(this.plugin.teamColor.get(hisTeam - 1) + "§lMembers of Team " + hisTeam);
                boolean role;
                for (String name : members) {
                    role = this.plugin.db.getMemberRolebyName(name);
                    if (role) {
                        player.sendMessage("§6[Lord]§r" + name);
                    } else {
                        player.sendMessage("§a[Civilian]§r" + name);
                    }

                }
            } else if (split[0].equalsIgnoreCase("promote")) {

                if (this.plugin.db.getMemberRole(player.getUniqueId().toString())) {
                    if (split.length > 1) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(split[1]);
                        if (!target.isWhitelisted​() || !this.plugin.db.existsInDB(target.getUniqueId().toString())) {
                            player.sendMessage("This player is not even on this Server");
                            return true;
                        }

                        int hisTeam = this.plugin.getMemberShip(target.getUniqueId​().toString());
                        int yourTeam = this.plugin.getMemberShip(player.getUniqueId​().toString());
                        if (hisTeam != yourTeam) {
                            player.sendMessage("You are in different Teams you can not promote him !");
                            return true;
                        }

                        this.plugin.db.UpdateMemberRole(target.getUniqueId().toString());

                        if (yourTeam == 1) {
                            this.plugin.factionTeam1.promote(target.getUniqueId().toString());
                        } else {
                            this.plugin.factionTeam2.promote(target.getUniqueId().toString());
                        }


                        player.sendMessage("User role updated");
                    } else {
                        player.sendMessage("Pls add the name of the player you want to promote / demote");
                    }
                } else {
                    player.sendMessage("You don't have the permission too do that!");
                }
            } else if (split[0].equalsIgnoreCase("add")) {
                if (this.plugin.db.getMemberRole(player.getUniqueId().toString())) {
                    if (split.length > 1) {

                        OfflinePlayer target = Bukkit.getOfflinePlayer(split[1]);
                        if (target.isWhitelisted​() || this.plugin.db.existsInDB(target.getUniqueId().toString())) {
                            player.sendMessage("This player is already on the server.");
                            return true;
                        }
                        target.setWhitelisted(true);
                        Bukkit.getServer().reloadWhitelist();
                        int hisTeam = this.plugin.getMemberShip(player.getUniqueId​().toString());
                        this.plugin.db.addToTeam(target.getName(), target.getUniqueId​().toString(), hisTeam);
                        player.sendMessage("Successfully whitelisted");

                    } else {
                        player.sendMessage("Pls add the name of the player you want to add");
                    }
                } else {
                    player.sendMessage("You don't have the permission too do that!");
                }

            } else if (split[0].equalsIgnoreCase("forceadd") && player.isOp()) {
                /*
                 This command should only be used for adding the two initial team leaders
                 */
                if (split.length > 1) {

                    String name = split[1];
                    int yourTeam = Integer.parseInt(split[2]);
                    if (yourTeam != 1 && yourTeam != 2) {
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(name);
                    registerUser(target, yourTeam); // Creates new User
                    this.plugin.db.UpdateMemberRole(target.getUniqueId().toString()); // Makes him Lord
                    if (yourTeam == 1) {
                        this.plugin.factionTeam1.promote(target.getUniqueId().toString());
                    } else {
                        this.plugin.factionTeam2.promote(target.getUniqueId().toString());
                    }
                }

            } else if (split[0].equalsIgnoreCase("forceleader") && player.isOp()) {
                if (split.length > 2) {

                    String name = split[1];
                    int yourTeam = this.plugin.getMemberShip(player.getUniqueId​().toString());
                    OfflinePlayer target = Bukkit.getOfflinePlayer(name);
                    this.plugin.db.UpdateMemberRole(target.getUniqueId().toString()); // Makes him Lord
                    if (yourTeam == 1) {
                        this.plugin.factionTeam1.promote(target.getUniqueId().toString());
                    } else {
                        this.plugin.factionTeam2.promote(target.getUniqueId().toString());
                    }
                }

            } else if (split[0].equalsIgnoreCase("kick")) {
                if (this.plugin.db.getMemberRole(player.getUniqueId().toString())) {
                    if (split.length > 1) {

                        OfflinePlayer target = Bukkit.getOfflinePlayer(split[1]);
                        if (!target.isWhitelisted​() || !this.plugin.db.existsInDB(target.getUniqueId().toString())) {
                            player.sendMessage("This player is already kicked");
                            return true;
                        }
                        int hisTeam = this.plugin.getMemberShip(target.getUniqueId​().toString());
                        int yourTeam = this.plugin.getMemberShip(player.getUniqueId​().toString());
                        if (hisTeam != yourTeam) {
                            player.sendMessage("You are in different Teams you can not kick him !");
                            return true;
                        }

                        target.setWhitelisted(false);
                        Bukkit.getServer().reloadWhitelist();
                        this.plugin.db.deleteFromTeam(target.getUniqueId().toString());
                        this.plugin.removePlayer(target.getUniqueId().toString(), hisTeam);
                        sender.sendMessage("Player kicked from whitelist");
                    } else {
                        player.sendMessage("Pls add the name of the player you want to kick");
                    }
                } else {
                    player.sendMessage("You don't have the permission too do that!");
                }

            }

        }
        return true;
    }
}