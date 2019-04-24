package swingy.mvc.models;

import swingy.util.Constants;

import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.awt.*;

public class Character {
    @Pattern(regexp = "^[0-9A-Za-z]+", message = "Only digits and letters in name")
    @Size(min = 3, max = 12, message = "Size of name must be 3-12 symbols length")
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

    private Artifact artifact;

    public Character() {
        position = new Point(0, 0);
        previousPosition = new Point(0, 0);
    }

    public String getInfo() {
        return ("\n Type: " + type + "\n\n Level: " + level + "\n\n Exp: " + exp
        + "\n\n Attack: " + attack + "\n\n Defense: " + defense + "\n\n Hit points: " + hitPoint);
    }

    public void move(int x, int y) {
        previousPosition.setLocation(position.x, position.y);
        position.setLocation(position.x + x, position.y + y);
    }

    public int getNecessaryExp() {
        return (int)(level * 1000 + Math.pow(level - 1, 2) * 450);
    }

    public void setHitPoint(int hp) {
        if (hp < 0) {
            hitPoint = 0;
        } else if (hp > maxHp) {
            hitPoint = maxHp;
        } else {
            hitPoint = hp;
        }
    }

    public int getFinalAttack() {
        if (artifact != null && artifact.getType().equals(Constants.ATTACK_STR)) {
            return ((attack + artifact.getValue()) << 2);
        }
        return (attack << 2);
    }

    public int getFinalDefense() {
        if (artifact != null && artifact.getType().equals(Constants.DEFENSE_STR)) {
            return (defense + artifact.getValue());
        }
        return defense;
    }

    public static class Builder {
        private Character character;

        public Builder() {
            character = new Character();
        }

        public Builder withType(String type) {
            character.setType(type);
            return this;
        }

        public Builder withLevel(int level) {
            character.setLevel(level);
            return this;
        }

        public Builder withExp(int exp) {
            character.setExp(exp);
            return this;
        }

        public Builder withAttack(int attack) {
            character.setAttack(attack);
            return this;
        }

        public Builder withDefense(int defense) {
            character.setDefense(defense);
            return this;
        }

        public Builder withMaxHp(int maxHp) {
            character.setMaxHp(maxHp);
            return this;
        }

        public Builder withHitPoint(int hitPoint) {
            character.setHitPoint(hitPoint);
            return this;
        }

        public Character build() {
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

    public Artifact getArtifact() {
        return artifact;
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

    public void setPreviousPosition(Point previousPosition) {
        this.previousPosition = previousPosition;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }
}