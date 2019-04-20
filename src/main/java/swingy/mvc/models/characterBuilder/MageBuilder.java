package swingy.mvc.models.characterBuilder;

import swingy.mvc.models.Character;
import swingy.util.Constants;

public class MageBuilder implements IBuilder {
    public void buildDefaultStats(Character character) {
        character.setType(Constants.MAGE_TYPE);
        character.setLevel(1);
        character.setExp(0);
        character.setAttack(40);
        character.setDefense(20);
        character.setMaxHp(150);
        character.setHitP(150);
    }
}
