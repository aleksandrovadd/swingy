package swingy.mvc;

import lombok.Getter;
import lombok.Setter;
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

public class Controller
{
    private IView currentGui;

    private Character character;
    private int sizeMap;
    private ArrayList<Enemy> enemies;
    private Random rand;

    /************** Constructor *****************/

    public Controller() {
        this.enemies = new ArrayList<>();
        this.sizeMap = 0;
        this.rand = new Random();
        this.currentGui = null;
    }

    /************* Game ******************/

    public  void    startGame(String argument) throws Exception {
        if (!argument.equals("gui") && !argument.equals("console"))
            throw new IOException("bad argument [gui or console]");
        this.changeGui(argument);
        if (this.character == null) {
            this.currentGui.ChooseHero();
            this.sizeMap = (this.character.getLevel() - 1) * 5 + 10 - (this.character.getLevel() % 2);
            this.initNewGame();
        }
        this.currentGui.drawGameObjects();
    }

    /**************** Public methods for View *****************/

    public void    keyPressed(int key) {
        this.handleKey(key);
        this.handleCollisions();

        this.currentGui.updateData();
        this.currentGui.viewRepaint();

        if (sizeMap > 9)
            currentGui.scrollPositionManager();
    }

    public void    saveHero()
    {
        if (currentGui.simpleDialog("Save your character ?"))
            DataBase.getDb().updateHero(this.character);
    }

    /****************** Private methods *****************/

    private void    handleKey(int key)
    {
        switch (key)
        {
            case 37:
                if (this.character.getPosition().x - 1 >= 0)
                    this.character.move(-1, 0);
                else
                    key = -1; break;
            case 38:
                if (this.character.getPosition().y - 1 >= 0)
                    this.character.move(0, -1);
                else key = -1; break;

            case 39:
                if (this.character.getPosition().x + 1 < this.sizeMap)
                    this.character.move(1, 0);
                else key = -1; break;

            case 40:
                if (this.character.getPosition().y + 1 < this.sizeMap)
                    this.character.move(0, 1);
                else key = -1; break;
            case -2:
                try {
                    String type = currentGui.get_Type().equals("gui") ? "console" : "gui";
                    currentGui.close();
                    this.startGame(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        if (key == -1) {
            if (currentGui.simpleDialog("End of map, start a new game?"))
                this.initNewGame();
            else {
                this.saveHero();
                System.exit(0);
            }
        }
    }

    /*************** Game logic ******************/

    private void    handleCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().equals(character.getPosition())) {
                manageBattle(enemy);
            }
        }
    }

    private void    initNewGame() {
        /* Character initialization */

        this.character.getPosition().setLocation( sizeMap >> 1, sizeMap >> 1);
        this.character.setHitP( character.getMaxHp() );

        /* Enemies initialization */

        this.dropEnemies();
    }

    private void    manageBattle(Enemy enemy) {
        Point enemyPos = enemy.getPosition();
        this.currentGui.addLog("You met an opponent:\n    hp: " + enemy.getHp() + "\n    attack: "
                + enemy.getAttack() + "\n    defense: " + enemy.getDefense());
        if (currentGui.simpleDialog("Do you want a battle ?"))
            this.battleAlgorithm(enemy);
        else {
            if (rand.nextInt(2) % 2 == 0) {
                this.character.setPosition(new Point(this.character.getOldPosition()));
                this.currentGui.addLog("You are lucky to escape");
            }
            else {
                this.currentGui.addLog("Run away failed");
                this.battleAlgorithm(enemy);
            }
        }
        if (!enemyPos.equals(character.getPosition()))
            return ;
        if (enemy.getHp() <= 0) {
            this.character.setExp( character.getExp() + ( (enemy.getAttack() + enemy.getDefense()) << 3 ) );
            this.currentGui.addLog("Opponent killed! Raised " + (enemy.getAttack() + enemy.getDefense() << 3) + " experience !" );
            this.enemies.remove(enemy);
            this.manageCharacter(enemy);
        }
        else
            character.setPosition(new Point(character.getOldPosition()));
    }

    private void    battleAlgorithm(Enemy enemy) {
        if (rand.nextInt(7) == 6) {
            enemy.setHp(0);
            this.currentGui.addLog("Critical hit !!!");
        }
        else {
            this.character.setHitP( character.getHitP() - (enemy.getAttack() << 2) + character.getFinalDefense() );
            if (this.checkDeath())
                return;
            enemy.setHp( enemy.getHp() - character.getFinalAttack() + enemy.getDefense());
            int raisedDamage = (enemy.getAttack() << 2) - character.getFinalDefense();
            this.currentGui.addLog("You caused " + (character.getFinalAttack() - enemy.getDefense())
                    + " damage to the opponent !\n" + (raisedDamage < 0 ? " Blocked up all" : " Raised " + raisedDamage ) + " damage.");
        }
    }

    private void manageCharacter(Enemy enemy)
    {
        if (character.getExp() >= character.getNeccesaryExp()) {
            currentGui.addLog("Level up ! Skills increased !");
            character.setMaxHp( character.getMaxHp() + (4 << character.getLevel()) );
            character.setHitP( character.getMaxHp() );
            character.setAttack( character.getAttack() + (character.getLevel() << 2) );
            character.setDefense( character.getDefense() + (character.getLevel() << 1) );
            character.setLevel( character.getLevel() + 1 );
            sizeMap = (character.getLevel() - 1) * 5 + 10 - (character.getLevel() % 2);
            this.dropEnemies();
        }
        this.manageBonuses(enemy);
    }

    private boolean   checkDeath() {
        if (character.getHitP() <= 0 ) {
            currentGui.updateData();
            if (currentGui.simpleDialog("You died, respawn at center of map ?"))
                initNewGame();
            else {
                saveHero();
                System.exit(0);
            }
            return true;
        }
        return false;
    }

    private void    manageBonuses(Enemy enemy) {
        if (rand.nextInt(3) == 2) {
            if (rand.nextInt(2) == 0) {
                int up = rand.nextInt(30) + 5;
                character.setHitP(character.getHitP() + up);
                currentGui.addLog("Found health elixir + " + up + " hp !");
            }
            else
                this.manageArtifacts(enemy);
        }
    }

    private void    dropEnemies() {
        this.enemies.clear();
        EnemyBuilder enemyBuilder = new EnemyBuilder();
        for (int i = rand.nextInt(sizeMap) + sizeMap; i > 0; i--)
            this.enemies.add(enemyBuilder.buildEnemy(sizeMap, enemies, character));
    }

    private void  manageArtifacts(Enemy enemy) {
        String artifact = rand.nextInt(2) == 0 ? "attack" : "defense";
        int    value = ((artifact.equals("attack") ? enemy.getAttack() : enemy.getDefense()) >> 1) + 1;
        if ( currentGui.simpleDialog("Found " + artifact + " artifact (" + value + ") pick it up ?")) {
            character.setArtifact( new Artifact(artifact, value) );
            currentGui.addLog("New artifact equipped");
        }
    }

    /**************** Gui changing ****************************/

    private void    changeGui(String guiName) {
        this.currentGui = guiName.equals("gui") ? new SwingView(this) : new ConsoleView(this);
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