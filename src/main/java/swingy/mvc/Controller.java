package swingy.mvc;

import swingy.bd.DataBase;
import swingy.mvc.models.Artifact;
import swingy.mvc.models.Enemy;
import swingy.mvc.models.EnemyBuilder;
import swingy.mvc.views.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import swingy.mvc.models.Character;
import swingy.mvc.views.console.*;
import swingy.mvc.views.swing.*;

public class Controller {
    private IView currentGui;

    private Character character;
    private int sizeMap;
    private ArrayList<Enemy> enemies;
    private Random rand;

    /************** Constructor *****************/

    public Controller() {
        enemies = new ArrayList<>();
        sizeMap = 0;
        rand = new Random();
        currentGui = null;
    }

    /************* Game ******************/

    public void startGame(String argument) throws Exception {
        if (!argument.equals("gui") && !argument.equals("console")) {
            throw new IOException("bad argument [gui or console]");
        }
        changeGui(argument);
        if (character == null) {
            currentGui.ChooseHero();
            sizeMap = (character.getLevel() - 1) * 5 + 10 - (character.getLevel() % 2);
            initNewGame();
        }
        currentGui.drawGameObjects();
    }

    /**************** Public methods for View *****************/

    public void keyPressed(int key) {
        handleKey(key);
        handleCollisions();

        currentGui.updateData();
        currentGui.viewRepaint();

        if (sizeMap > 9)
            currentGui.scrollPositionManager();
    }

    public void saveHero() {
        if (currentGui.simpleDialog("Save your character ?"))
            DataBase.getDb().updateHero(character);
    }

    /****************** Private methods *****************/

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
                    String type = currentGui.getViewType().equals("gui") ? "console" : "gui";
                    currentGui.close();
                    startGame(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        if (key == -1) {
            if (currentGui.simpleDialog("End of map, start a new game?"))
                initNewGame();
            else {
                saveHero();
                System.exit(0);
            }
        }
    }

    /*************** Game logic ******************/

    private void handleCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().equals(character.getPosition())) {
                manageBattle(enemy);
            }
        }
    }

    private void initNewGame() {
        /* Character initialization */

        character.getPosition().setLocation( sizeMap >> 1, sizeMap >> 1);
        character.setHitP( character.getMaxHp() );

        /* Enemies initialization */

        dropEnemies();
    }

    private void manageBattle(Enemy enemy) {
        Point enemyPos = enemy.getPosition();
        currentGui.addLog("You met an opponent:\n    hp: " + enemy.getHp() + "\n    attack: "
                + enemy.getAttack() + "\n    defense: " + enemy.getDefense());
        if (currentGui.simpleDialog("Do you want a battle ?")) {
            battleAlgorithm(enemy);
        } else {
            if (rand.nextInt(2) % 2 == 0) {
                character.setPosition(new Point(character.getOldPosition()));
                currentGui.addLog("You are lucky to escape");
            }
            else {
                currentGui.addLog("Run away failed");
                battleAlgorithm(enemy);
            }
        }
        if (!enemyPos.equals(character.getPosition())) {
            return;
        }
        if (enemy.getHp() <= 0) {
            character.setExp( character.getExp() + ( (enemy.getAttack() + enemy.getDefense()) << 3 ) );
            currentGui.addLog("Opponent killed! Raised " + (enemy.getAttack() + enemy.getDefense() << 3) + " experience !" );
            enemies.remove(enemy);
            manageCharacter(enemy);
        }
        else
            character.setPosition(new Point(character.getOldPosition()));
    }

    private void battleAlgorithm(Enemy enemy) {
        if (rand.nextInt(7) == 6) {
            enemy.setHp(0);
            currentGui.addLog("Critical hit !!!");
        }
        else {
            character.setHitP( character.getHitP() - (enemy.getAttack() << 2) + character.getFinalDefense() );
            if (checkDeath()) {
                return;
            }
            enemy.setHp( enemy.getHp() - character.getFinalAttack() + enemy.getDefense());
            int raisedDamage = (enemy.getAttack() << 2) - character.getFinalDefense();
            currentGui.addLog("You caused " + (character.getFinalAttack() - enemy.getDefense())
                    + " damage to the opponent !\n" + (raisedDamage < 0 ? " Blocked up all" : " Raised " + raisedDamage ) + " damage.");
        }
    }

    private void manageCharacter(Enemy enemy) {
        if (character.getExp() >= character.getNeccesaryExp()) {
            currentGui.addLog("Level up ! Skills increased !");
            character.setMaxHp( character.getMaxHp() + (4 << character.getLevel()) );
            character.setHitP( character.getMaxHp() );
            character.setAttack( character.getAttack() + (character.getLevel() << 2) );
            character.setDefense( character.getDefense() + (character.getLevel() << 1) );
            character.setLevel( character.getLevel() + 1 );
            sizeMap = (character.getLevel() - 1) * 5 + 10 - (character.getLevel() % 2);
            dropEnemies();
        }
        manageBonuses(enemy);
    }

    private boolean checkDeath() {
        if (character.getHitP() <= 0 ) {
            currentGui.updateData();
            if (currentGui.simpleDialog("You died, respawn at center of map ?")) {
                initNewGame();
            } else {
                saveHero();
                System.exit(0);
            }
            return true;
        }
        return false;
    }

    private void manageBonuses(Enemy enemy) {
        if (rand.nextInt(3) == 2) {
            if (rand.nextInt(2) == 0) {
                int up = rand.nextInt(30) + 5;
                character.setHitP(character.getHitP() + up);
                currentGui.addLog("Found health elixir + " + up + " hp !");
            }
            else {
                manageArtifacts(enemy);
            }
        }
    }

    private void dropEnemies() {
        enemies.clear();
        EnemyBuilder enemyBuilder = new EnemyBuilder();
        for (int i = rand.nextInt(sizeMap) + sizeMap; i > 0; i--) {
            enemies.add(enemyBuilder.buildEnemy(sizeMap, enemies, character));
        }
    }

    private void manageArtifacts(Enemy enemy) {
        String artifact = rand.nextInt(2) == 0 ? "attack" : "defense";
        int value = ((artifact.equals("attack") ? enemy.getAttack() : enemy.getDefense()) >> 1) + 1;
        if (currentGui.simpleDialog("Found " + artifact + " artifact (" + value + ") pick it up ?")) {
            character.setArtifact( new Artifact(artifact, value) );
            currentGui.addLog("New artifact equipped");
        }
    }

    /**************** Gui changing ****************************/

    private void changeGui(String guiName) {
        currentGui = guiName.equals("gui") ? new SwingView(this) : new ConsoleView(this);
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

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
}