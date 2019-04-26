package swingy.mvc.models;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MonsterBuilder {

    public Monster buildMonster(int sizeMap, ArrayList<Monster> monsters, Character character) {
        Point position = new Point(-1, -1);
        Monster newMonster = new Monster();

        while (position.x == -1) {
            position.setLocation(ThreadLocalRandom.current().nextInt(sizeMap), ThreadLocalRandom.current().nextInt(sizeMap));

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
        setSkills(newMonster, character.getLevel() * 2);

        return newMonster;
    }

    private void setSkills(Monster newMonster, int coefficient) {
        coefficient = (int) (coefficient * 0.5);
        if (coefficient <= 0) {
            coefficient = 1;
        }

        newMonster.setAttack((ThreadLocalRandom.current().nextInt(10, 30) * coefficient));
        newMonster.setDefense((ThreadLocalRandom.current().nextInt(10,25) * coefficient));
        newMonster.setHp((ThreadLocalRandom.current().nextInt(75, 250) * coefficient));
        newMonster.setNumImg(ThreadLocalRandom.current().nextInt(7));
    }
}