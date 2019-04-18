package swingy.mvc.models;

import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.awt.*;

public class Character {
    @Pattern(regexp = "^[0-9A-Za-z]+", message = "Only digits and letters in name")
    @Size(min = 4, max = 12, message = "Size of name must be 4-12 symbols length")
    private String  name;
    private String  type;
    private int     level;
    private int     exp;
    private int     attack;
    private int     defense;
    private int     maxHp;
    private int     hitP;

    private Point   position;
    private Point   oldPosition;

    private Artifact artifact;


    public Character() {
        this.position = new Point(0, 0);
        this.oldPosition = new Point(0, 0);
        this.artifact = null;
    }

    public String   getInfo() {
        return ("\n Type: " + type + "\n\n Level: " + level + "\n\n Exp: " + exp
        + "\n\n Attack: " + attack + "\n\n Defense: " + defense + "\n\n Hit points: " + hitP);
    }

    public void     move(int x, int y) {
        this.oldPosition.setLocation(this.position.x, this.position.y);
        this.position.setLocation(this.position.x + x, this.position.y + y);
    }

    public int      getNeccesaryExp() {
        return (int)(level * 1000 + Math.pow(level - 1, 2) * 450);
    }

    public void     setHitP(int hp) {
        if (hp < 0)
            hitP = 0;
        else if (hp > maxHp)
            hitP = maxHp;
        else
            hitP = hp;
    }

    public int      getFinalAttack() {
        if ( artifact != null && artifact.getType().equals("attack") )
            return ( (attack + artifact.getValue()) << 2 );

        return (attack << 2);
    }

    public int      getFinalDefense() {
        if ( artifact != null && artifact.getType().equals("defense") )
            return ( defense + artifact.getValue() );

        return defense;
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
        return hitP;
    }

    public Point getPosition() {
        return position;
    }

    public Point getOldPosition() {
        return oldPosition;
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

    public void setOldPosition(Point oldPosition) {
        this.oldPosition = oldPosition;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }
}