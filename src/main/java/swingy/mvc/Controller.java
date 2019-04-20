package swingy.mvc;

import swingy.bd.DataBase;
import swingy.mvc.models.Artifact;
import swingy.mvc.models.Monster;
import swingy.mvc.models.MonsterBuilder;
import swingy.mvc.views.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import swingy.mvc.models.Character;
import swingy.mvc.views.console.*;
import swingy.mvc.views.swing.*;
import swingy.util.Constants;

public class Controller {
    private IView currentGui;

    private Character character;
    private int sizeMap;
    private ArrayList<Monster> monsters;
    private Random rand;

    public Controller() {
        monsters = new ArrayList<>();
        sizeMap = 0;
        rand = new Random();
        currentGui = null;
    }

    public void startGame(String argument) throws Exception {
        if (!argument.equals(Constants.GUI_STR) && !argument.equals(Constants.CONSOLE_STR)) {
            throw new IOException("Please, use a correct argument [gui or console]");
        }
        changeGui(argument);
        if (character == null) {
            currentGui.ChooseCharacter();
            sizeMap = (character.getLevel() - 1) * 5 + 10 - (character.getLevel() % 2);
            initNewGame();
        }
        currentGui.drawGameObjects();
    }

    public void keyPressed(int key) {
        handleKey(key);
        handleCollisions();

        currentGui.updateData();
        currentGui.viewRepaint();

        if (sizeMap > 9)
            currentGui.scrollPositionManager();
    }

    public void saveCharacter() {
        if (currentGui.simpleDialog("Save your character?"))
            DataBase.getDb().updateCharacter(character);
    }

    private void handleKey(int key) {
        switch (key) {
            case 37:
                if (character.getPosition().x - 1 >= 0) {
                    character.move(-1, 0);
                } else {
                    key = -1;
                }
                break;
            case 38:
                if (character.getPosition().y - 1 >= 0) {
                    character.move(0, -1);
                } else {
                    key = -1;
                }
                break;
            case 39:
                if (character.getPosition().x + 1 < sizeMap) {
                    character.move(1, 0);
                } else {
                    key = -1;
                }
                break;
            case 40:
                if (character.getPosition().y + 1 < sizeMap) {
                    character.move(0, 1);
                } else {
                    key = -1;
                }
                break;
            case -2:
                try {
                    String type = currentGui.getViewType().equals(Constants.GUI_STR) ? Constants.CONSOLE_STR : Constants.GUI_STR;
                    currentGui.close();
                    startGame(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        if (key == -1) {
            if (currentGui.simpleDialog("End of the map, start a new game?")) {
                initNewGame();
            }
            else {
                saveCharacter();
                System.exit(0);
            }
        }
    }

    private void handleCollisions() {
        for (int i = 0; i < monsters.size(); i++) {
            if (monsters.get(i).getPosition().equals(character.getPosition())) {
                manageBattle(monsters.get(i));
            }
        }
    }

    private void initNewGame() {
        character.getPosition().setLocation( sizeMap >> 1, sizeMap >> 1);
        character.setHitP( character.getMaxHp() );

        this.updateMonsters();
    }

    private void manageBattle(Monster monster) {
        Point monsterPosition = monster.getPosition();
        currentGui.addLog("You met an monster:\n    hp: " + monster.getHp() + "\n    attack: "
                + monster.getAttack() + "\n    defense: " + monster.getDefense());
        if (currentGui.simpleDialog("Do you want to attack him?")) {
            battleAlgorithm(monster);
        } else {
            if (rand.nextInt(2) % 2 == 0) {
                character.setPosition(new Point(character.getPreviousPosition()));
                currentGui.addLog("You are lucky to escape");
            }
            else {
                currentGui.addLog("You failed to run away");
                battleAlgorithm(monster);
            }
        }
        if (!monsterPosition.equals(character.getPosition())) {
            return;
        }
        if (monster.getHp() <= 0) {
            character.setExp( character.getExp() + ( (monster.getAttack() + monster.getDefense()) << 3 ) );
            currentGui.addLog("Monster was killed! Raised " + (monster.getAttack() + monster.getDefense() << 3) + " experience !" );
            this.monsters.remove(monster);
            manageCharacter(monster);
        }
        else
            character.setPosition(new Point(character.getPreviousPosition()));
    }

    private void battleAlgorithm(Monster monster) {
        if (rand.nextInt(7) == 6) {
            monster.setHp(0);
            currentGui.addLog("Critical hit!!!");
        }
        else {
            character.setHitP(character.getHitP() - (monster.getAttack() << 2) + character.getFinalDefense());
            if (checkDeath()) {
                return;
            }
            monster.setHp( monster.getHp() - character.getFinalAttack() + monster.getDefense());
            int raisedDamage = (monster.getAttack() << 2) - character.getFinalDefense();
            currentGui.addLog("You caused " + (character.getFinalAttack() - monster.getDefense())
                    + " damage to the monster !\n" + (raisedDamage < 0 ? " Blocked up all damage" : " Raised " + raisedDamage ) + " damage.");
        }
    }

    private void manageCharacter(Monster monster) {
        if (character.getExp() >= character.getNecessaryExp()) {
            currentGui.addLog("Level up ! Skills increased!");
            character.setMaxHp( character.getMaxHp() + (4 << character.getLevel()) );
            character.setHitP( character.getMaxHp() );
            character.setAttack( character.getAttack() + (character.getLevel() << 2));
            character.setDefense( character.getDefense() + (character.getLevel() << 1));
            character.setLevel( character.getLevel() + 1 );
            sizeMap = (character.getLevel() - 1) * 5 + 10 - (character.getLevel() % 2);
            this.updateMonsters();
        }
        manageBonuses(monster);
    }

    private boolean checkDeath() {
        if (character.getHitP() <= 0 ) {
            currentGui.updateData();
            if (currentGui.simpleDialog("You have failed, you died, respawn at the center of map ?")) {
                initNewGame();
            } else {
                saveCharacter();
                System.exit(0);
            }
            return true;
        }
        return false;
    }

    private void manageBonuses(Monster monster) {
        if (rand.nextInt(3) == 2) {
            if (rand.nextInt(2) == 0) {
                int up = rand.nextInt(80) + 10;
                character.setHitP(character.getHitP() + up);
                currentGui.addLog("Health elixir was found + " + up + " hp!");
            }
            else {
                manageArtifacts(monster);
            }
        }
    }

    private void updateMonsters() {
        this.monsters.clear();
        MonsterBuilder monsterBuilder = new MonsterBuilder();
        for (int i = rand.nextInt(sizeMap) + sizeMap; i > 0; i--) {
            this.monsters.add(monsterBuilder.buildMonster(sizeMap, monsters, character));
        }
    }

    private void manageArtifacts(Monster monster) {
        String artifact = rand.nextInt(2) == 0 ? "attack" : Constants.DEFENSE_STR;
        int value = ((artifact.equals("attack") ? monster.getAttack() : monster.getDefense()) >> 1) + 1;
        if (currentGui.simpleDialog("Found " + artifact + " artifact (" + value + ") pick it up ?")) {
            character.setArtifact( new Artifact(artifact, value) );
            currentGui.addLog("New artifact equipped");
        }
    }

    private void changeGui(String guiName) {
        currentGui = guiName.equals(Constants.GUI_STR) ? new SwingView(this) : new ConsoleView(this);
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public int getSizeMap() {
        return sizeMap;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }
}