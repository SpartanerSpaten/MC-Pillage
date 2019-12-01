package com.einspaten.bukkit.mcpillage;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarCommand implements CommandExecutor {

    private final MCPillagePlugin plugin;

    public WarCommand(MCPillagePlugin plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        Player player = (Player) sender;

        if(split.length > 0){
            if(split[0].equalsIgnoreCase("start")){

                if(!player.isOp()){
                    player.sendMessage("You dont have the permission to do that.");
                    return true;
                }

                if(split.length > 1){
                    try
                    {
                        int i = Integer.parseInt(split[1]);
                        if(i != 1 && i != 2){
                            player.sendMessage("Invalid Team Number should be 1 or 2.");
                            return true;
                        }
                        plugin.eventManager.startFight(i, 3);
                    }
                    catch (NumberFormatException nfe)
                    {
                        player.sendMessage("Invalid Team Number should be 1 or 2. Example: /war start 1");
                    }
                }


            } else if(split[0].equalsIgnoreCase("stop")){
                if(!player.isOp()){
                    player.sendMessage("You dont have the permission to do that.");
                    return true;
                }
                plugin.eventManager.stopWar();
                player.sendMessage("Stoped war.");

            }else if(split[0].equalsIgnoreCase("info")){
                if(plugin.eventManager.getWar()){
                    player.sendMessage("=================== War Information ===================");
                    player.sendMessage("War will end at: " + plugin.eventManager.printEnd());
                    player.sendMessage("Currently Attacking Team: " + Integer.toString(plugin.eventManager.getCurrentlyAttacking()));
                }else{
                    player.sendMessage("There is no war currently going on.");
                }

            }
            Bukkit.broadcastMessage(split[0]);
        } else{
            player.sendMessage("The command /war has following options start <teamID>, stop, info");
        }

        return true;
    }
}