package swingy.mvc.models.characterBuilder;

import swingy.mvc.models.Character;
import swingy.util.Constants;

public class WarriorBuilder implements IBuilder {
    public void buildDefaultStats(Character character) {
        character.setType(Constants.WARRIOR_TYPE);
        character.setLevel(1);
        character.setExp(0);
        character.setAttack(20);
        character.setDefense(80);
        character.setMaxHp(200);
        character.setHitP(200);
    }
}
