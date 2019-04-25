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

    private static Statement statement;
    private static ResultSet resultSet;

    private Connection connection;

    public static DataBase getInstance() {
        if (dataBase == null) {
            dataBase = new DataBase();
        }
        return dataBase;
    }

    public void connectDatabase() throws Exception {
        if (connection == null) {

            String createTable = "CREATE  TABLE if not EXISTS 'characters' ('name' text, 'type' text, 'level' INT, 'exp' INT," +
                    "'attack' INT, 'defense' INT, 'hp' INT, 'maxHp' INT, 'artifactT' text, 'artifactV' INT);";

            try {
                Class.forName(DRIVER_NAME);
                connection = DriverManager.getConnection(CONNECTION_STRING);
                statement = connection.createStatement();
                statement.execute(createTable);
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
        }
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        try {
            String selectCharactersQuery = "SELECT * FROM characters";
            resultSet = statement.executeQuery(selectCharactersQuery);
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
        } catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return names;
    }

    public void insertCharacter(Character newCharacter) {
        try {
            String artifactType = newCharacter.getArtefact() == null ? "" : newCharacter.getArtefact().getType();
            int artifactValue = artifactType.equals("") ? 0 : newCharacter.getArtefact().getValue();

            String requestAdd = "VALUES ('" + newCharacter.getName() + "', '" + newCharacter.getType() + "', " + newCharacter.getLevel() + "," +
                    newCharacter.getExp() + "," + newCharacter.getAttack() + "," + newCharacter.getDefense() + "," + newCharacter.getHitP() + ","
                    + newCharacter.getMaxHp() + ",'" + artifactType + "'," + artifactValue + ");";

            String insertQuery = "INSERT INTO 'characters' ('name', 'type', 'level', 'exp', 'attack', 'defense', 'hp', 'maxHP', 'artifactT', 'artifactV')";
            statement.execute(insertQuery + requestAdd);
        } catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void deleteCharacter(String name) {
        try {
            String deleteQuery = "DELETE FROM characters WHERE name = '" + name + "';";
            statement.execute(deleteQuery);
        } catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public Character selectCharacter(String name) throws Exception {
        try {
            String selectQuery = "SELECT * FROM characters where name = '" + name + "';";
            resultSet = statement.executeQuery(selectQuery);
            return resultSet.next() ? CharacterBuilder.buildByInfo(resultSet) : null;
        } catch(SQLException sqlEx) {
            sqlEx.printStackTrace();
            return null;
        }
    }

    public void updateCharacter(Character character) {
        String request = "UPDATE characters SET level = " + character.getLevel() + ", exp = " + character.getExp() +
                ", attack = " + character.getAttack() + ", defense = " + character.getDefense() + ", hp = " + character.getMaxHp() +
                ", maxHp = " + character.getMaxHp() + ", artifactT = '" + ( character.getArtefact() == null ? "" : character.getArtefact().getType() ) +
                "' , artifactV = " + character.getArtefact().getValue() + " WHERE name = '" + character.getName() + "';";

        try {
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}