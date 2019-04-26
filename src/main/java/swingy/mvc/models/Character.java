package swingy.mvc.models;

import swingy.util.Constants;

import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.awt.*;

public class Character {
    @Pattern(regexp = "^[0-9A-Za-z]+", message = "Only letters and digits in name")
    @Size(min = 3, max = 12, message = "Length of name must be 3-12 symbols")
    private String name;
    private String type;
    private int level = 1;
    private int exp = 0;
    private int attack;
    private int defense;
    private int maxHp;
    private int hitPoint;

    private Point position;
    private Point previousPosition;

    private Artefact artefact;

    Character() {
        position = new Point(0, 0);
        previousPosition = new Point(0, 0);
    }

    public void changePosition(int x, int y) {
        previousPosition.setLocation(position.x, position.y);
        position.setLocation(position.x + x, position.y + y);
    }

    public int getNecessaryExp() {
        return (int)(level * 1000 + Math.pow(level - 1, 2) * 450);
    }

    public void setHitPoint(int hitP) {
        if (hitP < 0) {
            hitPoint = 0;
        } else if (hitP > maxHp) {
            hitPoint = maxHp;
        } else {
            hitPoint = hitP;
        }
    }

    public int getFinalAttack() {
        if (artefact != null && artefact.getType().equals(Constants.ATTACK_STR)) {
            return ((attack + artefact.getValue()) * 4);
        }
        return (attack * 4);
    }

    public int getFinalDefense() {
        if (artefact != null && artefact.getType().equals(Constants.DEFENSE_STR)) {
            return (defense + artefact.getValue());
        }
        return defense;
    }

    static class Builder {
        private Character character;

        Builder() {
            character = new Character();
        }

        Builder withType(String type) {
            character.setType(type);
            return this;
        }

        Builder withAttack(int attack) {
            character.setAttack(attack);
            return this;
        }

        Builder withDefense(int defense) {
            character.setDefense(defense);
            return this;
        }

        Builder withMaxHp(int maxHp) {
            character.setMaxHp(maxHp);
            return this;
        }

        Builder withHitPoint(int hitPoint) {
            character.setHitPoint(hitPoint);
            return this;
        }

        Character build() {
            return character;
        }

    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getHitP() {
        return hitPoint;
    }

    public Point getPosition() {
        return position;
    }

    public Point getPreviousPosition() {
        return previousPosition;
    }

    public Artefact getArtefact() {
        return artefact;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setArtefact(Artefact artefact) {
        this.artefact = artefact;
    }

    @Override
    public String toString() {
        return ("\n Type: " + type + "\n\n Level: " + level + "\n\n Exp: " + exp
                + "\n\n Attack: " + attack + "\n\n Defense: " + defense + "\n\n Hit points: " + hitPoint);
    }
}