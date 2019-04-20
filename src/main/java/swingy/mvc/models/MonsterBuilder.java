package swingy.mvc.models;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MonsterBuilder {
    public Monster buildMonster(int sizeMap, ArrayList<Monster> monsters, Character character) {
        Random rand = new Random();
        Point position = new Point(-1, -1);
        Monster newMonster = new Monster();

        while (position.x == -1) {
            position.setLocation(rand.nextInt(sizeMap), rand.nextInt(sizeMap));

            if (position.equals(character.getPosition())) {
                position.x = -1;
            }

            for (Monster monster : monsters) {
                if (monster.getPosition().equals(position)) {
                    position.x = -1;
                }
            }
        }
        newMonster.setPosition(position);
        setSkills(newMonster, rand, character.getLevel() << 1);

        return newMonster;
    }

    private void    setSkills(Monster newMonster, Random rand, int coefficient) {
//        coefficient = coefficient == 2 ? 1 : coefficient;

        coefficient = (int) (coefficient * 0.5);
        if (coefficient <= 0)
            coefficient = 1;

        newMonster.setAttack((rand.nextInt(20) + 10) * coefficient);
        newMonster.setDefense((rand.nextInt(15) + 10) * coefficient);
        newMonster.setHp((rand.nextInt(150) + 75) + 10 * coefficient);
        newMonster.setNumImg(rand.nextInt(7));
    }
}