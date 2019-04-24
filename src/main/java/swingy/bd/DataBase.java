package swingy.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import swingy.mvc.models.CharacterBuilder;
import swingy.mvc.models.Character;

public class DataBase {

    private static final String DRIVER_NAME = "org.sqlite.JDBC";
    private static final String CONNECTION_STRING =  "jdbc:sqlite:../characters.db";

    private static DataBase dataBase;

    private static Statement statm;
    private static ResultSet info;

    private Connection connection;

    public static DataBase getDataBase() {
        if (dataBase == null) {
            dataBase = new DataBase();
        }
        return dataBase;
    }

    public void connectDb() throws Exception {
        if (connection == null) {

            String createTable = "CREATE  TABLE if not EXISTS 'characters' ('name' text, 'type' text, 'level' INT, 'exp' INT," +
                    "'attack' INT, 'defense' INT, 'hp' INT, 'maxHp' INT, 'artifactT' text, 'artifactV' INT);";
            Class.forName(DRIVER_NAME);

            try {
                connection = DriverManager.getConnection(CONNECTION_STRING);
                statm = connection.createStatement();
                statm.execute(createTable);
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
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
        int artifactValue = artifactType == "" ? 0 : newCharacter.getArtifact().getValue();

        String requestAdd = "VALUES ('" + newCharacter.getName() + "', '" + newCharacter.getType() + "', " + newCharacter.getLevel() + "," +
                newCharacter.getExp() + "," + newCharacter.getAttack() + "," + newCharacter.getDefense() + "," + newCharacter.getHitP() + ","
                + newCharacter.getMaxHp() + ",'" + artifactType + "'," + artifactValue + ");";

        statm.execute("INSERT INTO 'characters' ('name', 'type', 'level', 'exp', 'attack', 'defense', 'hp', 'maxHP', 'artifactT', 'artifactV')" + requestAdd );
    }

    public void remove(String name) throws Exception {
        statm.execute("DELETE FROM characters WHERE name = '" + name + "';");
    }

    public Character getCharacter(String name) throws Exception {
        info = statm.executeQuery("SELECT * FROM characters where name = '" + name + "';");
        return info.next() ? CharacterBuilder.buildByType(info.toString()) : null;
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