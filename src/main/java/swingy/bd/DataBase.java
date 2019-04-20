package swingy.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import swingy.mvc.models.characterBuilder.CharacterBuilder;
import swingy.mvc.models.Character;

public class DataBase {
    private static DataBase db = null;

    private static Statement    statm;
    private static ResultSet    info;
    private final String        driverName;
    private final String        connectionString;
    private Connection          connection;

    private DataBase() {
        this.driverName  = "org.sqlite.JDBC";
        this.connectionString = "jdbc:sqlite:../characters.db";
        this.connection = null;
    }

    public static DataBase  getDb() {
        if (db == null) {
            db = new DataBase();
        }

        return db;
    }

    public void connectDb() throws Exception {
        if (connection == null) {
            Class.forName(driverName);
            connection = DriverManager.getConnection(connectionString);

            statm = this.connection.createStatement();
            statm.execute("CREATE  TABLE if not EXISTS 'characters' ('name' text, 'type' text, 'level' INT, 'exp' INT," +
                    "'attack' INT, 'defense' INT, 'hp' INT, 'maxHp' INT, 'artifactT' text, 'artifactV' INT);");
        }
    }

    public List<String> getNames() throws Exception {
        info = statm.executeQuery("SELECT * FROM characters");
        List<String> names = new ArrayList<>();
                while (info.next()) {
            names.add(info.getString("name"));
        }
        return names;
    }

    public void addNewCharacter(Character newCharacter) throws Exception {
        String artifactType = newCharacter.getArtifact() == null ? "" : newCharacter.getArtifact().getType();
        int    artifactValue = artifactType == "" ? 0 : newCharacter.getArtifact().getValue();

        String  requestAdd = "VALUES ('" + newCharacter.getName() + "', '" + newCharacter.getType() + "', " + newCharacter.getLevel() + "," +
                newCharacter.getExp() + "," + newCharacter.getAttack() + "," + newCharacter.getDefense() + "," + newCharacter.getHitP() + ","
                + newCharacter.getMaxHp() + ",'" + artifactType + "'," + artifactValue + ");";

        statm.execute("INSERT INTO 'characters' ('name', 'type', 'level', 'exp', 'attack', 'defense', 'hp', 'maxHP', 'artifactT', 'artifactV')" + requestAdd );
    }

    public void remove(String name) throws Exception {
        statm.execute("DELETE FROM characters WHERE name = '" + name + "';");
    }

    public Character getCharacter(String name) throws Exception {
        info = statm.executeQuery("SELECT * FROM characters where name = '" + name + "';");
        return info.next() ? new CharacterBuilder().buildByInfo(info) : null;
    }

    public void updateCharacter(Character character) {
        String request = "UPDATE characters SET level = " + character.getLevel() + ", exp = " + character.getExp() +
                ", attack = " + character.getAttack() + ", defense = " + character.getDefense() + ", hp = " + character.getMaxHp() +
                ", maxHp = " + character.getMaxHp() + ", artifactT = '" + ( character.getArtifact() == null ? "" : character.getArtifact().getType() ) +
                "' , artifactV = " + character.getArtifact().getValue() + " WHERE name = '" + character.getName() + "';";

        try {
            statm.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}