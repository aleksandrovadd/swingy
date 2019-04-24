package swingy.mvc.models;

import javax.validation.*;
import java.sql.ResultSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static swingy.util.Constants.*;

public class CharacterBuilder {

    private CharacterBuilder() { }

    public static Character buildByType(String type) {
        Character newCharacter = new Character();

        switch (type) {
            case WARRIOR_TYPE:
                newCharacter = new Character.Builder()
                        .withType(ROGUE_TYPE)
                        .withAttack(30)
                        .withDefense(80)
                        .withMaxHp(200)
                        .withHitPoint(200)
                        .build();
                break;
            case MAGE_TYPE:
                newCharacter = new Character.Builder()
                        .withType(ROGUE_TYPE)
                        .withAttack(40)
                        .withDefense(20)
                        .withMaxHp(150)
                        .withHitPoint(150)
                        .build();
                break;
            case ROGUE_TYPE:
                newCharacter = new Character.Builder()
                        .withType(ROGUE_TYPE)
                        .withAttack(30)
                        .withDefense(25)
                        .withMaxHp(175)
                        .withHitPoint(175)
                        .build();
                break;
        }
        return newCharacter;
    }

    public static String setName(Character character, String newName) {
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);

        character.setName(newName);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Character>> violations = validator.validate(character);

        for (ConstraintViolation<Character> violation : violations) {
            return violation.getMessage();
        }
        return null;
    }

    public Character buildByInfo(ResultSet info) throws Exception {
        Character newCharacter = new Character();

        newCharacter.setName(info.getString("name"));
        newCharacter.setType(info.getString("type"));
        newCharacter.setArtifact( new Artifact(info.getString("artifactT"), info.getInt("artifactV")) );
        newCharacter.setAttack(info.getInt(ATTACK_STR) );
        newCharacter.setDefense(info.getInt(DEFENSE_STR));
        newCharacter.setExp(info.getInt("exp"));
        newCharacter.setLevel(info.getInt("level"));
        newCharacter.setMaxHp(info.getInt("maxHp"));
        newCharacter.setHitPoint(info.getInt("hp"));

        return newCharacter;
    }

}