package swingy.mvc.models.characterBuilder;

import swingy.mvc.models.Character;
import swingy.util.Constants;

public class RogueBuilder implements IBuilder {
    public void buildDefaultStats(Character character) {
        character.setType(Constants.ROGUE_TYPE);
        character.setLevel(1);
        character.setExp(0);
        character.setAttack(30);
        character.setDefense(20);
        character.setMaxHp(175);
        character.setHitP(175);
    }
}