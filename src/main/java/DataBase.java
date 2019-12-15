package com.einspaten.bukkit.mcpillage;

import java.sql.*;
import java.util.ArrayList;


public class DataBase {

    // Database Stuff
    private Connection connection;
    private String host, database, username, password, file;
    private int port;

    DataBase() {
        host = "localhost";
        port = 3306;
        database = "mcpillage";
        username = "plugin";
        password = "secretpw";
        file = "./testdb.db";

        try {
            openConnection();
            createTables();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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
        String sqlCreateTableUser = "CREATE TABLE IF NOT EXISTS users (\n"
                + "    uuid VARCHAR(32) PRIMARY KEY,\n"
                + "    name TEXT NOT NULL,\n"
                + "    faction INTEGER NOT NULL,\n"
                + "    role BIT DEFAULT 0\n" // We only want 2 roles <Citizen and Lord>
                + ");";
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sqlCreateTableUser);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean existsInDB(String uuid) {
        String SelectCommand = "SELECT COUNT(*) FROM users WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            // get the number of rows from the result set
            query.next();
            return query.getInt(1) > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }


    }


    public boolean addToTeam(String name, String uuid, int faction) {

        String InsertCommand = "INSERT INTO users (uuid, name, faction, role) VALUES ('" + uuid + "', '" + name + "', " + faction + ", 0 );";
        try {
            Statement stmt = this.connection.createStatement();
            int countInserted = stmt.executeUpdate(InsertCommand);
            return countInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteFromTeam(String uuid) {

        String DeleteCommand = "DELETE FROM users WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            int countInserted = stmt.executeUpdate(DeleteCommand);
            return countInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean getMemberRole(String uuid) {
        String SelectCommand = "SELECT role FROM users WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            return query.getBoolean("role");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean getMemberRolebyName(String name) {
        String SelectCommand = "SELECT role FROM users WHERE name == '" + name + "';";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            return query.getBoolean("role");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public int getTeam(String uuid) {
        String SelectCommand = "SELECT faction FROM users WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);
            return query.getInt("faction");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }


    public boolean UpdateMemberRole(String uuid) {
        boolean current = getMemberRole(uuid);

        String UpdateCommand = "UPDATE users SET role = " + !current + " WHERE uuid == '" + uuid + "';";
        try {
            Statement stmt = this.connection.createStatement();
            int countInserted = stmt.executeUpdate(UpdateCommand);
            return countInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> getMembers(int team) {
        String SelectCommand = "SELECT uuid FROM users WHERE faction == " + team + ";";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);

            ArrayList<String> data = new ArrayList<String>();

            while (query.next()) {
                data.add(query.getString("uuid"));
            }

            return data;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return new ArrayList<String>();
        }
    }

    public ArrayList<String> getMembersbyName(int team) {
        String SelectCommand = "SELECT name FROM users WHERE faction == " + team + ";";
        try {
            Statement stmt = this.connection.createStatement();
            ResultSet query = stmt.executeQuery(SelectCommand);

            ArrayList<String> data = new ArrayList<String>();

            while (query.next()) {
                data.add(query.getString("name"));
            }

            return data;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return new ArrayList<String>();
        }
    }


}