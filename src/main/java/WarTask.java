package com.einspaten.bukkit.mcpillage;

import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class WarTask extends BukkitRunnable {

    public static MCPillagePlugin plugin;
    static LocalTime time = LocalTime.of(19, 30, 0);
    static java.time.DayOfWeek attackDay = DayOfWeek.SATURDAY;
    private static int lastAttacker = 1;


    public WarTask() {
    }

    public static void setPlugin(MCPillagePlugin mcplugin) {
        plugin = mcplugin;
    }

    @Override
    public void run() {
        if (checkEvent()) {
            if (lastAttacker == 1) {
                lastAttacker = 2;
            } else {
                lastAttacker = 1;
            }

            plugin.eventManager.startFight(lastAttacker);
        }
    }

    public boolean checkEvent() {

        LocalTime now = LocalTime.now();
        LocalDate localDate = LocalDate.now();

        if (attackDay == localDate.getDayOfWeek()) {

            if (plugin.eventManager.getWar()) {
                return false;
            }
            // Bukkit.getLogger().info("Time: " + Math.abs(MINUTES.between(now, time)));
            return Math.abs(MINUTES.between(now, time)) < 0;
        }

        return false;


    }

}