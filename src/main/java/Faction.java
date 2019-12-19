package com.einspaten.bukkit.mcpillage;

import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Faction {


    public HashMap<String, Boolean> memberRole = new HashMap<String, Boolean>();
    int sizeXPositive = 250;
    int sizeXNegative = 250;
    int sizeZPositive = 250;
    int sizeZNegative = 250;

    int spawnSizeX = 7;
    int middleX, middleZ, team;

    private final MCPillagePlugin plugin;
    int spawnSizeZ = 7;
    List<String> members;
    ArrayList<Player> onlineMembers;

    public Faction(int posx, int posz, int iTeam, MCPillagePlugin plugin) {
        middleX = posx;
        middleZ = posz;
        team = iTeam;
        members = new ArrayList<>();
        onlineMembers = new ArrayList<>();
        this.plugin = plugin;
        members = this.plugin.db.getMembers(this.team);
        loadRoles();
    }

    public void loadRoles() {
        boolean hisRole;
        for (String uuid : members) {
            hisRole = this.plugin.db.getMemberRole(uuid);
            memberRole.put(uuid, hisRole);
        }
    }

    public void loadRegions() {
        ResultSet set = this.plugin.db.getRegionInformation(team == 1);
        try {
            sizeZPositive = set.getInt("sizezpositive");
            sizeZNegative = set.getInt("sizeznegative");
            if (team == 2) {
                sizeXPositive = 250;
                sizeZNegative = set.getInt("sizex");
            } else {
                sizeXPositive = set.getInt("sizex");
                sizeXNegative = 250;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void increaseSize() {
        // Use setter and now only save the data
        //  this.plugin.db.updateRegion(team == 1, sizeZPositive, sizeZNegative, )
    }


    public boolean getRoleUUid(String uuid) {
        Boolean role = memberRole.get(uuid);
        if (role != null) {
            return role;
        } else {
            return false;
        }
    }

    public void promote(String uuid) {
        boolean current = getRoleUUid(uuid);
        memberRole.put(uuid, !current);
    }

    public void attackBegin() {
        sizeXNegative += 100;
        sizeXPositive += 100;
        sizeZNegative += 100;
        sizeZPositive += 100;
    }

    public void attackStop() {
        sizeXNegative -= 100;
        sizeXPositive -= 100;
        sizeZNegative += 100;
        sizeZPositive += 100;
    }

    public int getMiddleX() {
        return middleX;
    }

    public int getMiddleZ() {
        return middleZ;
    }

    public boolean permissions(int x, int z) {
        return (x < middleX + sizeXPositive && x > middleX - sizeXNegative) && (z < middleZ + sizeZPositive && z > middleZ - sizeZNegative);
    }

    public boolean atSpawn(int x, int z) {
        return (x < middleX + spawnSizeX && x > middleX - spawnSizeX) && (z < middleZ + spawnSizeZ && z > middleZ - spawnSizeZ);
    }

    public boolean isMember(String uuid) {
        return members.contains(uuid);
    }

    public void addMember(String uuid) {
        members.add(uuid);
    }

    public void removeMember(String deleteUuid) {
        int i = 0;
        for (String uuid : members) {
            if (uuid.equals(deleteUuid)) {
                members.remove(i);
                return;
            }
            i++;
        }
    }

    public void addOnlinePlayer(Player player) {
        onlineMembers.add(player);
    }

    public void removeOnlinePlayer(Player player) {
        int i = 0;
        for (Player playerIterator : onlineMembers) {
            if (playerIterator.getUniqueId() == player.getUniqueId()) {
                try {
                    members.remove(i);
                    return;
                } catch (Exception e) {
                    return;
                }

            }
            i++;
        }
    }

    public ArrayList<Player> getOnlineMembers() {
        return onlineMembers;
    }


}