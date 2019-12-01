package com.einspaten.bukkit.mcpillage;

import java.util.List;
import java.util.ArrayList;

public class Faction{

    int sizeX = 250;
    int sizeZ = 250;
    int middleX = 2500;
    int middleZ = 0;
    int team;
    private final MCPillagePlugin plugin;

    List<String> members;

    public Faction(int posx, int posz, int iTeam, MCPillagePlugin plugin){
        middleX = posx;
        middleZ = posz;
        team = iTeam;
        members = new ArrayList<String>();
        this.plugin = plugin;
    }

    public void attackBegin(){
        sizeX += 100;
        sizeZ += 100;
    }

    public void attackStop(){
        sizeX -= 100;
        sizeZ -= 100;
    }

    public void setSizeX(int x){
        sizeX = x;
    }
    public void setSizeZ(int z){
        sizeZ = z;
    }
    public int getMiddleX(){ return middleX; }
    public int getMiddleZ(){ return middleZ; }
    public int getSizeX(){return sizeX;}
    public int getSizeZ(){return sizeZ;}

    public boolean permissions(int x, int z){
        return (x < middleX + sizeX && x > middleX - sizeX) && (z < middleZ + sizeZ && z > middleZ - sizeZ);
    }
    public boolean isMember(String username){
        return members.contains(username);
    }
    public void addMember(String username){
        members.add(username);
    }



}