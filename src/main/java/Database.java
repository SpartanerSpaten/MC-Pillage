package com.einspaten.bukkit.mcpillage;

import org.bukkit.Bukkit;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;


public class Database {

    // Database Stuff
    private Connection connection;
    private String host, database, username, password, file;
    private int port;

    Database() {
        host = "localhost";
        port = 3306;
        database = "mcpillage";
        username = "plugin";
        password = "secretpw";
        file = "./mcpillage.db";

        try {
            openConnection();
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void reload() {
        Bukkit.getLogger().info("Reloading Database");
        close();
        try {
            openConnection();
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (this.connection != null && !this.connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (this.connection != null && !this.connection.isClosed()) {
                return;
            }
            Class.forName("org.sqlite.JDBC");
            // connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file);
        }
    }

    public void createTables() {
        String sql_create_table_user = "CREATE TABLE IF NOT EXISTS users ("
                + "    uuid VARCHAR(32) PRIMARY KEY,"
                + "    name TEXT NOT NULL,"
                + "    guild INTEGER DEFAULT 0," // For Later so we don't need migration
                + "    role INTEGER DEFAULT 0,"
                + "    money INTEGER DEFAULT 2500,"
                + "    daily_reward LONG NOT NULL DEFAULT 0," // Saves Unix Time stamp and gives money every 24 hours
                + "    friendly_role INTEGER DEFAULT 0," // <1: Miner, 2: Farmer, 3: WoodHacker, 4: Brewer, 5:Enchanter, 6:Traveler> // Evt. Later
                + "    combat_role INTEGER DEFAULT 0" // <1: Warrior, 2:Archer, 3: Mage, 4: Supporter> // Evt. Later
                + ");";

        String sql_create_table_home = "CREATE TABLE IF NOT EXISTS home ("
                + "    uuid VARCHAR(32) PRIMARY KEY,"
                + "    pos_x FLOAT NOT NULL,"
                + "    pos_y FLOAT NOT NULL,"
                + "    pos_z FLOAT NOT NULL,"
                + "    dimension INTEGER NOT NULL DEFAULT 0" // Used for later when we allow building plots in the nether and evt. end
                + ");";
        String sql_create_table_plot = "CREATE TABLE IF NOT EXISTS plot ("
                + "    plot_id INTEGER AUTO_INCREMENT PRIMARY KEY,"
                + "    uuid VARCHAR(32) NOT NULL,"
                + "    owner BIT DEFAULT 0,"
                + "    dimension INTEGER NOT NULL DEFAULT 0,"
                + "    size INTEGER NOT NULL,"
                + "    pos_x INTEGER NOT NULL,"
                + "    pos_z INTEGER NOT NULL"
                + ");";

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql_create_table_user);
            stmt.executeUpdate(sql_create_table_home);
            stmt.executeUpdate(sql_create_table_plot);
            stmt.close();
        } catch (SQLException ex) {
            reload();
            ex.printStackTrace();
        }
    }

    public boolean existsInDB(String uuid) {
        String SelectCommand = "SELECT COUNT(*) FROM users WHERE uuid == '" + uuid + "';";
        com.einspaten.bukkit.mcpillage.PluginPlayer player = new com.einspaten.bukkit.mcpillage.PluginPlayer(uuid);

        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            // get the number of rows from the result set
            query.next();
            boolean exists = query.getInt(1) > 0;
            stmt.close();

            return exists;

        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }

    public boolean addPlayer(String name, String uuid) {

        //com.einspaten.bukkit.mcpillage.PluginPlayer player = new com.einspaten.bukkit.mcpillage.PluginPlayer(name);

        String insert_user = "INSERT INTO users (uuid, name) VALUES ('" + uuid + "', '" + name + "');";
        String insert_home = "INSERT INTO home (uuid, pos_x, pos_y, pos_z) VALUES ('" + uuid + "', -1, -1, -1);";

        try {
            Statement stmt = this.connection.createStatement();
            int count_users = stmt.executeUpdate(insert_user);
            int count_home = stmt.executeUpdate(insert_home);
            stmt.close();
            return count_users > 0 && count_home > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }

    private void setDailyReward(String uuid, long time_stamp) {
        String UpdateCommand = "UPDATE users SET daily_reward = " + time_stamp + " WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            int countInserted = stmt.executeUpdate(UpdateCommand);
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
        }
    }


    public boolean daily_reward(String uuid) {
        String SelectCommand = "SELECT daily_reward FROM users WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query_user = stmt.executeQuery(SelectCommand);
            long last_time = query_user.getLong("daily_reward");
            long unixTime = Instant.now().getEpochSecond();

            if (unixTime - last_time > 86400) {// 24 * 60 * 60 -> One day in seconds
                setDailyReward(uuid, unixTime);
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
        }
        return false;
    }

    public boolean removePlayer(String uuid) {

        String DeleteCommand = "DELETE FROM users WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            int countInserted = stmt.executeUpdate(DeleteCommand);
            stmt.close();
            return countInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }

    public com.einspaten.bukkit.mcpillage.PluginPlayer loadPlayer(String uuid) {
        String SelectHome = "SELECT pos_x, pos_y, pos_z FROM home WHERE uuid == '" + uuid + "';";
        String SelectUser = "SELECT money, role FROM users WHERE uuid == '" + uuid + "';";
        com.einspaten.bukkit.mcpillage.PluginPlayer player = new com.einspaten.bukkit.mcpillage.PluginPlayer(uuid);

        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectHome);

            if (!query.isClosed()) {
                player.setHome_x(query.getInt("pos_x"));
                player.setHome_y(query.getInt("pos_y"));
                player.setHome_z(query.getInt("pos_z"));
            }
            query = stmt.executeQuery(SelectUser);
            player.setRole(query.getInt("role"));
            int money = query.getInt("money");
            player.increaseMoney(money);
            player.setPlayer_plots(this.loadPlot(uuid));
            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
        }
        return player;
    }


    public boolean updateHome(String uuid, int pos_x, int pos_y, int pos_z) {

        String UpdateCommand = "UPDATE home SET pos_x = " + pos_x + ", pos_y = " + pos_y + ", pos_z = " + pos_z + " WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            int countInserted = stmt.executeUpdate(UpdateCommand);
            stmt.close();
            return countInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }

    public Plot loadPlot(int pos_x, int pos_z) {
        String SelectCommand = "SELECT pos_x, pos_z, size, owner FROM plot WHERE"
                + " pos_x + size > " + pos_x
                + " AND pos_x - size - 1< " + pos_x
                + " AND pos_z + size > " + pos_z
                + " AND pos_z - size - 1 < " + pos_z + " ;";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query_plot = stmt.executeQuery(SelectCommand);
            if (!query_plot.isClosed()) {
                query_plot.next();
                Plot plot = new Plot(query_plot.getInt("size"), query_plot.getInt("pos_x"), query_plot.getInt("pos_z"), query_plot.getBoolean("owner"));
                stmt.close();
                return plot;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
        }
        return null;
    }

    public ArrayList<Plot> loadPlot(String uuid) {
        String get_all_plots = "SELECT pos_x, pos_z, size, owner FROM plot WHERE uuid == '" + uuid + "';";
        ArrayList<Plot> data = new ArrayList<Plot>();
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query_plot = stmt.executeQuery(get_all_plots);

            while (query_plot.next()) {
                data.add(new Plot(query_plot.getInt("size"), query_plot.getInt("pos_x"), query_plot.getInt("pos_z"), query_plot.getBoolean("owner")));
            }
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
        }
        return data;
    }

    boolean setMoney(String uuid, int money) {
        String UpdateCommand = "UPDATE users SET money = money + " + money + " WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            int countInserted = stmt.executeUpdate(UpdateCommand);
            stmt.close();
            return countInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }


    public boolean addPlot(com.einspaten.bukkit.mcpillage.Plot plot, String uuid, boolean owner) {
        String insert_plot = "INSERT INTO plot (uuid, size, pos_x, pos_z, owner) VALUES ('" + uuid + "', " + plot.getSize() + ", " + plot.getPos_x() + ", " + plot.getPos_z() + ", " + owner + " );";

        try {
            Statement stmt = this.connection.createStatement();
            int count_plot = stmt.executeUpdate(insert_plot);
            stmt.close();
            return count_plot > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }

    public boolean removePlot(String uuid, Plot plot) {

        String DeleteCommand = "DELETE FROM plot WHERE uuid == '" + uuid + "' AND pos_x = " + plot.getPos_x() + " AND pos_z = " + plot.getPos_z() + ";";
        try {
            Statement stmt = this.connection.createStatement();
            int count = stmt.executeUpdate(DeleteCommand);
            return count > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }

    public boolean sellPlot(Plot plot) {

        String DeleteCommand = "DELETE FROM plot WHERE pos_x = " + plot.getPos_x() + " AND pos_z = " + plot.getPos_z() + ";";
        try {
            Statement stmt = this.connection.createStatement();
            int count = stmt.executeUpdate(DeleteCommand);
            return count > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }


    public int getMemberRole(String uuid) {
        String SelectCommand = "SELECT role FROM users WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            stmt.close();
            return query.getInt("role");

        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return 0;
        }
    }

    public int getMemberRolebyName(String name) {
        String SelectCommand = "SELECT role FROM users WHERE name == '" + name + "';";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            stmt.close();
            return query.getInt("role");

        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return -1;
        }
    }

    public boolean UpdateMemberRole(String uuid, int newRole) {

        String UpdateCommand = "UPDATE users SET role = " + newRole + " WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            int countInserted = stmt.executeUpdate(UpdateCommand);
            stmt.close();
            return countInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return false;
        }
    }

    public String resolveUsername(String name) {
        String SelectCommand = "SELECT * FROM users WHERE name == '" + name + "';";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            query.next();
            if (!query.isClosed()) {
                return query.getString("uuid");
            } else {
                Bukkit.getLogger().info("Query is closed !");
            }
            stmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
        }

        return null;
    }

    public boolean checkColliding(int pos_x, int pos_z, int size) {
        String SelectCommand = "SELECT COUNT(*) FROM plot WHERE"
                + " pos_x + size > " + (pos_x - size)
                + " AND pos_x - size < " + (pos_x + size)
                + " AND pos_z + size > " + (pos_z - size)
                + " AND pos_z - size < " + (pos_z + size) + ";";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            query.next();
            return query.getInt(1) > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            reload();
            return true;
        }
    }

}