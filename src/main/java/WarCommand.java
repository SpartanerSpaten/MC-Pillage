package com.einspaten.bukkit.mcpillage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarCommand implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public WarCommand(MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Player player = (Player) sender;

        if (split.length > 0) {
            if (split[0].equalsIgnoreCase("start")) {

                if (!player.isOp()) {
                    player.sendMessage("You don't have the permission to do that.");
                    return true;
                }

                if (split.length > 1) {
                    try {
                        int i = Integer.parseInt(split[1]);
                        if (i != 1 && i != 2) {
                            player.sendMessage("Invalid Team Number should be 1 or 2.");
                            return true;
                        }
                        plugin.eventManager.startFight(i);
                    } catch (NumberFormatException nfe) {
                        player.sendMessage("Invalid Team Number should be 1 or 2. Example: /war start 1");
                    }
                }


            } else if (split[0].equalsIgnoreCase("stop")) {
                if (!player.isOp()) {
                    player.sendMessage("You don't have the permission to do that.");
                    return true;
                }
                plugin.eventManager.stopWar();

            } else if (split[0].equalsIgnoreCase("info")) {
                if (plugin.eventManager.getWar()) {
                    int currentlyAttacking = plugin.eventManager.getCurrentlyAttacking();
                    player.sendMessage("**************** War Information ****************");
                    player.sendMessage("Currently Attacking: " + this.plugin.teamColor.get(currentlyAttacking - 1) + "Team" + currentlyAttacking);
                    player.sendMessage("War will end at: §d" + plugin.eventManager.printEnd());
                } else {
                    String teamString;
                    int currentlyAttacking = plugin.eventManager.getNextAttackingTeam();

                    if (currentlyAttacking == 1) {
                        teamString = "§cTeam Communism";
                    } else {
                        teamString = "§9RTeam Capitalism";
                    }

                    player.sendMessage("The next attacking Team is " + teamString);
                }
            } else if (split[0].equalsIgnoreCase("set") && player.isOp()) {
                plugin.eventManager.swapAttackingTeam();
            }
        } else {
            player.sendMessage("The command /war has following options start <teamID>, stop, info");
        }

        return true;
    }
}