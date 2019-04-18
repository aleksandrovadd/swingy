package swingy.mvc.models.heroBuilder;

import swingy.mvc.models.*;
import swingy.mvc.models.Character;

import javax.validation.*;
import java.sql.ResultSet;
import java.util.Set;
import java.util.logging.Level;

public class DirectorHero
{
    public DirectorHero()
    {
        this.builder = null;
    }

    public Character buildbyType(String type)
    {
        Character newHero = new Character();

        switch (type)
        {
            case "Human": this.builder = new HumanBuilder(); break;
            case "Ork":   this.builder = new OrkBuilder();   break;
            case "Elf":   this.builder = new ElfBuilder();   break;
        }
        this.builder.buildDefaultStats(newHero);

        return newHero;
    }

    public String setName(Character hero, String newName)
    {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);

        hero.setName(newName);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Character>> violations = validator.validate(hero);

        for (ConstraintViolation<Character> violation : violations) {
            return violation.getMessage();
        }
        return null;
    }

    public Character buildByInfo(ResultSet info) throws Exception
    {
        Character newHero = new Character();

        newHero.setName(info.getString("name"));
        newHero.setType(info.getString("type"));
        newHero.setArtifact( new Artifact(info.getString("artifactT"), info.getInt("artifactV")) );
        newHero.setAttack(info.getInt("attack") );
        newHero.setDefense(info.getInt("defense"));
        newHero.setExp(info.getInt("exp"));
        newHero.setLevel(info.getInt("level"));
        newHero.setMaxHp(info.getInt("maxHp"));
        newHero.setHitP(info.getInt("hp"));

        return newHero;
    }

    private IBuilder builder;
}