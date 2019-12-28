package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class WarTask extends BukkitRunnable {

    public static MCPillagePlugin plugin;
    static LocalTime time = LocalTime.of(17, 15, 0);
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
            plugin.eventManager.autoStart();
        }
    }

    public boolean checkEvent() {

        LocalTime now = LocalTime.now();
        LocalDate localDate = LocalDate.now();

        if (attackDay == localDate.getDayOfWeek()) {
            if (plugin.eventManager.getWar()) {
                return false;
            }
            long timeDifference = MINUTES.between(now, time);

            if (timeDifference == 1 || timeDifference == 5 || timeDifference == 30 || timeDifference == 60) {
                plugin.eventManager.alarmPlayer(timeDifference);
            }

            Bukkit.getLogger().info("Time" + timeDifference);
            return Math.abs(timeDifference) < 1;
        }

        return false;

    }

}