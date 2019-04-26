package swingy.mvc;

import swingy.bd.DataBase;
import swingy.mvc.models.Artefact;
import swingy.mvc.models.Monster;
import swingy.mvc.models.MonsterBuilder;
import swingy.mvc.views.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import swingy.mvc.models.Character;
import swingy.mvc.views.console.*;
import swingy.mvc.views.gui.*;
import swingy.util.Constants;

import static swingy.util.Constants.*;

public class Controller {
    private IView currentGui;

    private Character character;
    private int sizeMap = 0;
    private ArrayList<Monster> monsters;

    public Controller() {
        monsters = new ArrayList<>();
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
            setupNewGame();
        }
        currentGui.drawObjects();
    }

    public void keyPressed(int key) {
        handleKey(key);
        handleCollisions();

        currentGui.updateData();
        currentGui.reDraw();

        if (sizeMap > 9)
            currentGui.scrollPositionManager();
    }

    public void saveCharacter() {
        if (currentGui.yesNoDialog("Save your character?"))
            DataBase.getInstance().updateCharacter(character);
    }

    private void handleKey(int key) {
        switch (key) {
            case KEY_WEST:
                if (character.getPosition().x - 1 >= 0) {
                    character.changePosition(-1, 0);
                } else {
                    key = DEFAULT_INT;
                }
                break;
            case KEY_NORTH:
                if (character.getPosition().y - 1 >= 0) {
                    character.changePosition(0, -1);
                } else {
                    key = DEFAULT_INT;
                }
                break;
            case KEY_EAST:
                if (character.getPosition().x + 1 < sizeMap) {
                    character.changePosition(1, 0);
                } else {
                    key = DEFAULT_INT;
                }
                break;
            case KEY_SOUTH:
                if (character.getPosition().y + 1 < sizeMap) {
                    character.changePosition(0, 1);
                } else {
                    key = DEFAULT_INT;
                }
                break;
            case GUI_SWITCH:
                try {
                    String type = currentGui.getViewType().equals(Constants.GUI_STR) ? Constants.CONSOLE_STR : Constants.GUI_STR;
                    currentGui.close();
                    startGame(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        if (key == DEFAULT_INT) {
            if (currentGui.yesNoDialog("End of the map, start a new game?")) {
                setupNewGame();
            } else {
                saveCharacter();
                System.exit(0);
            }
        }
    }

    private void handleCollisions() {
        for (int i = 0; i < monsters.size(); i++) {
            if (monsters.get(i).getPosition().equals(character.getPosition())) {
                startBattle(monsters.get(i));
            }
        }
    }

    private void setupNewGame() {
        character.getPosition().setLocation( sizeMap / 2, sizeMap / 2);
        character.setHitPoint( character.getMaxHp() );

        updateMonsters();
    }

    private void startBattle(Monster monster) {
        Point monsterPosition = monster.getPosition();
        currentGui.addLog("You met an monster:\n    hp: " + monster.getHp() + "\n    attack: "
                + monster.getAttack() + "\n    defense: " + monster.getDefense());
        if (currentGui.yesNoDialog("Do you want to attack him?")) {
            battleAlgorithm(monster);
        } else {
            if (ThreadLocalRandom.current().nextInt(2) % 2 == 0) {
                character.setPosition(new Point(character.getPreviousPosition()));
                currentGui.addLog("You are lucky to escape");
            } else {
                currentGui.addLog("You failed to run away");
                battleAlgorithm(monster);
            }
        }
        if (!monsterPosition.equals(character.getPosition())) {
            return;
        }
        if (monster.getHp() <= 0) {
            character.setExp(character.getExp() + ((monster.getAttack() + monster.getDefense()) * 8 ));
            currentGui.addLog("Monster was killed! Raised " + (monster.getAttack() + monster.getDefense() * 8) + " experience !");
            monsters.remove(monster);
            handleCharacter(monster);
        } else {
            character.setPosition(new Point(character.getPreviousPosition()));
        }
    }

    private void battleAlgorithm(Monster monster) {
        if (ThreadLocalRandom.current().nextInt(7) == 6) {
            monster.setHp(0);
            currentGui.addLog("Critical hit!!!");
        } else {
            character.setHitPoint(character.getHitP() - (monster.getAttack() * 4) + character.getFinalDefense());
            if (checkDeath()) {
                return;
            }
            monster.setHp( monster.getHp() - character.getFinalAttack() + monster.getDefense());
            int raisedDamage = (monster.getAttack() * 4) - character.getFinalDefense();
            currentGui.addLog("You caused " + (character.getFinalAttack() - monster.getDefense())
                    + " damage to the monster !\n" + (raisedDamage <= 0 ? " Blocked up all damage" : " Raised " + raisedDamage ) + " damage.");
        }
    }

    private void handleCharacter(Monster monster) {
        if (character.getExp() >= character.getNecessaryExp()) {
            currentGui.addLog("Level up ! Skills increased!");
            character.setMaxHp(character.getMaxHp() + (int) (Math.pow(2,(character.getLevel() + 2))));
            character.setHitPoint(character.getMaxHp());
            character.setAttack(character.getAttack() + (character.getLevel() * 4));
            character.setDefense(character.getDefense() + (character.getLevel() * 2));
            character.setLevel(character.getLevel() + 1);
            sizeMap = (character.getLevel() - 1) * 5 + 10 - (character.getLevel() % 2);
            updateMonsters();
        }
        handleBonuses(monster);
    }

    private boolean checkDeath() {
        if (character.getHitP() <= 0 ) {
            currentGui.updateData();
            if (currentGui.yesNoDialog("You have failed, you died, respawn at the center of map ?")) {
                setupNewGame();
            } else {
                saveCharacter();
                System.exit(0);
            }
            return true;
        }
        return false;
    }

    private void handleBonuses(Monster monster) {
        if (ThreadLocalRandom.current().nextInt(3) == 2) {
            if (ThreadLocalRandom.current().nextInt(2) == 0) {
                int up = ThreadLocalRandom.current().nextInt(10, 90);
                character.setHitPoint(character.getHitP() + up);
                currentGui.addLog("Health elixir was found + " + up + " hp!");
            } else {
                manageArtifacts(monster);
            }
        }
    }

    private void updateMonsters() {
        monsters.clear();
        MonsterBuilder monsterBuilder = new MonsterBuilder();
        int i = ThreadLocalRandom.current().nextInt(sizeMap, sizeMap + sizeMap);
        for (; i > 0; i--) {
            monsters.add(monsterBuilder.buildMonster(sizeMap, monsters, character));
        }
    }

    private void manageArtifacts(Monster monster) {
        String artifact = ThreadLocalRandom.current().nextInt(2) == 0 ? "attack" : Constants.DEFENSE_STR;
        int value = ((artifact.equals("attack") ? monster.getAttack() : monster.getDefense()) / 2) + 1;
        if (currentGui.yesNoDialog("Found " + artifact + " artifact (" + value + ") pick it up ?")) {
            character.setArtefact( new Artefact(artifact, value) );
            currentGui.addLog("New artifact equipped");
        }
    }

    private void changeGui(String guiName) {
        currentGui = guiName.equals(Constants.GUI_STR) ? new GuiView(this) : new ConsoleView(this);
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