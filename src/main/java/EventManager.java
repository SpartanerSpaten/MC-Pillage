package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;

import java.time.LocalTime;


public class EventManager {

    private int nextAttackingTeam = 1;
    private int currentlyAttacking = -1;
    private LocalTime durationOfAttack = null;
    private LocalTime startOfAttack = null;

    private final com.einspaten.bukkit.mcpillage.MCPillagePlugin plugin;

    private boolean war = false;
    private int warduration = 30; // Mins

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

    public int getNextAttackingTeam() {
        return nextAttackingTeam;
    }

    private void checkForTimeout() {
        if (war && startOfAttack != null && durationOfAttack != null) {
            LocalTime now = LocalTime.now();
            if (now.compareTo(durationOfAttack) > 0) {
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
        String teamString;
        //createScoreboard();
        if (currentlyAttacking == 1) {
            teamString = "§cTeam Communism";
        } else {
            teamString = "§9Team Capitalism";
        }

        Bukkit.broadcastMessage("§b§k=== " + this.plugin.teamColor.get(currentlyAttacking - 1) + teamString + "§r Began to Attack §b§k===");
        Bukkit.broadcastMessage("Battle will end at: §d" + printEnd());
    }


    void autoStart() {
        startFight(nextAttackingTeam);
        swapAttackingTeam();
    }

    void swapAttackingTeam() {
        if (nextAttackingTeam == 1) {
            nextAttackingTeam = 2;
        } else {
            nextAttackingTeam = 1;
        }
    }

    void alarmPlayer(long timeDifference) {
        String team;
        if (nextAttackingTeam == 1) {
            team = "§cTeam Communism";
        } else {
            team = "§9RTeam Capitalism";
        }

        Bukkit.broadcastMessage("§b§k=== " + team + "§r Will attack in ~" + timeDifference + " Minutes §b§k===");
    }

    public boolean fightActive(int attacker) {
        return attacker == currentlyAttacking;
    }

    public String printEnd() {
        if (war && durationOfAttack != null) {
            return durationOfAttack.toString();
        }
        return "There is no war currently.";
    }
}