package com.einspaten.bukkit.mcpillage;

public class Plot {

    private int size = 50;
    private int pos_x = 0;
    private int pos_z = 0;
    private boolean owner = false;

    Plot(int size, int pos_x, int pos_z, boolean owner) {
        this.size = size;
        this.pos_x = pos_x;
        this.pos_z = pos_z;
        this.owner = owner;
    }

    Plot(int size, int pos_x, int pos_z) {
        this.size = size;
        this.pos_x = pos_x;
        this.pos_z = pos_z;
        this.owner = false;
    }

    boolean onHisPlot(int player_x, int player_z) {
        return (player_x < pos_x + size && player_x > pos_x - size - 1) && (player_z < pos_z + size && player_z > pos_z - size - 1);
    }

    public int getSize() {
        return size;
    }

    public int getPos_x() {
        return pos_x;
    }

    public int getPos_z() {
        return pos_z;
    }

    public boolean getOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }
}

