package swingy.mvc.models.characterBuilder;

import swingy.mvc.models.*;
import swingy.mvc.models.Character;

import javax.validation.*;
import java.sql.ResultSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static swingy.util.Constants.*;

public class CharacterBuilder {

    private IBuilder builder;

    public CharacterBuilder() {

    }

    public Character buildByType(String type) {
        Character newCharacter = new Character();

        switch (type) {
            case WARRIOR_TYPE: builder = new WarriorBuilder(); break;
            case MAGE_TYPE:   builder = new MageBuilder();   break;
            case ROGUE_TYPE:   builder = new RogueBuilder();   break;
        }
        builder.buildDefaultStats(newCharacter);

        return newCharacter;
    }

    public String setName(Character character, String newName) {
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
        newCharacter.setAttack(info.getInt("attack") );
        newCharacter.setDefense(info.getInt(DEFENSE_STR));
        newCharacter.setExp(info.getInt("exp"));
        newCharacter.setLevel(info.getInt("level"));
        newCharacter.setMaxHp(info.getInt("maxHp"));
        newCharacter.setHitP(info.getInt("hp"));

        return newCharacter;
    }

}