package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;

import java.time.LocalTime;


public class EventManager {

    private int currentlyAttacking = -1;
    private LocalTime durationOfAttack = null;
    private LocalTime startOfAttack = null;

    private final com.einspaten.bukkit.mcpillage.MCPillagePlugin plugin;

    private boolean war = false;
    private int warduration = 3; // Mins

    public EventManager(com.einspaten.bukkit.mcpillage.MCPillagePlugin plugin) {
        this.plugin = plugin;
    }

    public int getCurrentlyAttacking() {

        checkForTimeout();
        if (war) {
            return currentlyAttacking;
        }
        return -1;
    }

    private void checkForTimeout(){
        if(war && startOfAttack != null && durationOfAttack != null){
            LocalTime now = LocalTime.now();
            if(now.compareTo(durationOfAttack) > 0){
                stopWar();
            }
        }
    }

    public boolean getWar(){
        checkForTimeout();
        return war;
    }

    public void stopWar(){

        if(currentlyAttacking == 1){
            plugin.factionTeam2.attackStop();
        }else if(currentlyAttacking == 2){
            plugin.factionTeam1.attackStop();
        }

        war = false;
        currentlyAttacking = -1;
        durationOfAttack = null;
        startOfAttack = null;
    }

    void startFight(int team) {
        startOfAttack = LocalTime.now();
        durationOfAttack = startOfAttack.plusMinutes(warduration);
        currentlyAttacking = team;
        if (currentlyAttacking == 1) {
            plugin.factionTeam2.attackBegin();
        } else if (currentlyAttacking == 2) {
            plugin.factionTeam1.attackBegin();
        }
        war = true;
        //createScoreboard();
        Bukkit.broadcastMessage("§b§k=== " + this.plugin.teamColor.get(currentlyAttacking - 1) + "Team " + team + "§r Began to Attack §b§k===");
        Bukkit.broadcastMessage("Battel will end at: §d" + printEnd());
    }

    public boolean fightActive(int attacker){
        return attacker == currentlyAttacking;
    }

    public String printEnd(){
        if(war && durationOfAttack != null){
            return durationOfAttack.toString();
        }
        return "There is no war currently.";
    }
}