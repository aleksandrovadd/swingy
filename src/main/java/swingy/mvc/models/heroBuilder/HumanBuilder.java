package swingy.mvc.models.heroBuilder;

import swingy.mvc.models.Character;

public class HumanBuilder implements IBuilder {
    public void buildDefaultStats(Character hero) {
        hero.setType("human");
        hero.setLevel(1);
        hero.setExp(0);
        hero.setAttack(10);
        hero.setDefense(3);
        hero.setMaxHp(100);
        hero.setHitP(100);
    }
}
