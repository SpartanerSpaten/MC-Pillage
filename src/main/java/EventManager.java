package com.einspaten.bukkit.mcpillage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class EventManager {

    static java.time.DayOfWeek team1Attack = java.time.DayOfWeek.SUNDAY;
    static java.time.DayOfWeek team2Attack = java.time.DayOfWeek.FRIDAY;

    private int currentlyAttacking = -1;
    private int currentlydefening = -1;
    private LocalTime durationOfAttack = null;
    private LocalTime startOfAttack = null;

    private Scoreboard board;
    private Objective objective;
    private ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private boolean war = false;

    private final MCPillagePlugin plugin;

    public EventManager(MCPillagePlugin plugin){
        this.plugin = plugin;
    }

    public int getCurrentlyAttacking(){
        if(war){
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
            plugin.factionTeam1.attackStop();
        }else if(currentlyAttacking == 2){
            plugin.factionTeam2.attackStop();
        }

        war = false;
        currentlyAttacking = -1;
        currentlydefening = -1;
        durationOfAttack = null;
        startOfAttack = null;
    }


    public void checkEvent(){

        LocalDate localDate = LocalDate.now();

        java.time.DayOfWeek day = localDate.getDayOfWeek();

        if(day == team1Attack){
            startFight(1, 1);
        } else if (day == team2Attack){
            startFight(2, 1);
        }

    }

    void startFight(int team, int durationMin){
        startOfAttack = LocalTime.now();
        durationOfAttack = startOfAttack.plusMinutes(durationMin);
        currentlyAttacking = team;
        if(currentlyAttacking == 1){
            currentlydefening = 2;
            plugin.factionTeam1.attackBegin();
        }else if(currentlyAttacking == 2){
            currentlydefening = 1;
            plugin.factionTeam2.attackBegin();
        }
        war = true;
        //createScoreboard();
        Bukkit.broadcastMessage("============== Team " + Integer.toString(team) + " Began to Attack ===============");
        Bukkit.broadcastMessage("Battel will end at: " + printEnd());
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

    public void createScoreboard(){
        board = scoreboardManager.getNewScoreboard();

        Team team = board.registerNewTeam("teamname");

        Objective objective = board.registerNewObjective("showhealth", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName("/ 20");


        for(Player p : Bukkit.getOnlinePlayers()){

            p.setScoreboard(board);
            p.setHealth(p.getHealth()); //Update their health

        }

    }

}