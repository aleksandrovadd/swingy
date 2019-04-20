package swingy.mvc.views.swing;

import javax.swing.*;
import swingy.mvc.models.Character;
import swingy.util.Constants;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SwingStats extends JPanel
{
    private Character character;
    private Map<String, JLabel> stats;
    private Font font;
    private String res;

    public SwingStats(Character character) {
        this.character = character;
        this.stats = new HashMap<>();
        this.res = "resources/icons/";

        this.setLayout(null);
        this.setSize(325, 500);
        this.setLocation(50, 50);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.drawRect(1, 1, 323, 498);
    }

    public void updateData() {
        if (stats.size() == 0)
            this.prepareInfo();

        stats.get("name").setText("Name: " + (character.getName() == null ? "" : character.getName()) );
        stats.get("type").setText("Type: " + character.getType());

        if (character.getPosition() != null) {
            stats.get("location").setText("Location: [" + character.getPosition().x + ", " + character.getPosition().y + "]");
        }

        stats.get("level").setText("Level: " + character.getLevel());
        stats.get("exp").setText("Exp: " + character.getExp() + "/" + character.getNecessaryExp());
        stats.get("attack").setText(String.valueOf(character.getAttack()));
        stats.get(Constants.DEFENSE_STR).setText(" " + character.getDefense());
        stats.get("hp").setText(" " + character.getHitP() + "/" + character.getMaxHp());

        if (character.getArtifact() != null && !character.getArtifact().getType().equals("")) {
            stats.get("artifact").setIcon(new ImageIcon(
                    res + (character.getArtifact().getType() == Constants.ATTACK_STR ? "artifactA.png" : "artifactD.png") ));
            stats.get("artifact").setText(" " + character.getArtifact().getValue());
        }
        else {
            stats.get("artifact").setText("");
            stats.get("artifact").setIcon(null);
        }
    }

    private void prepareInfo()
    {
        this.loadFont();

        JLabel name = new JLabel("Name: ");
        name.setLocation(20, 5);
        name.setSize(225, 75);
        name.setFont(this.font);
        if (character.getName() != null) {
            name.setText("Name: " + character.getName());
        }

        JLabel type = new JLabel("Type: " + character.getType());
        type.setLocation(20, 55);
        type.setSize(150, 50);
        type.setFont(this.font);

        JLabel level = new JLabel("Level: " + character.getLevel());
        level.setLocation(20, 105);
        level.setSize(150, 50);
        level.setFont(this.font);

        JLabel location = new JLabel("Location: [ ]");
        location.setLocation(20, 155);
        location.setSize(200, 50);
        location.setFont(this.font);
        if (character.getPosition() != null) {
            location.setText("Location: [" + character.getPosition().x + ", " + character.getPosition().y + "]");
        }

        JLabel exp = new JLabel("Exp: " + character.getExp() + "/" + character.getNecessaryExp() );
        exp.setLocation(20, 205);
        exp.setSize(320, 50);
        exp.setFont(this.font);

        JLabel attack = new JLabel( String.valueOf(character.getAttack()), new ImageIcon(res + "sword.png"), JLabel.LEFT );
        attack.setLocation(20, 260);
        attack.setSize(200, 50);
        attack.setFont(this.font);

        JLabel defense = new JLabel( " " + character.getDefense(), new ImageIcon(res + "shield.png"), JLabel.LEFT );
        defense.setLocation(15, 315);
        defense.setSize(200, 50);
        defense.setFont(this.font);

        JLabel hp = new JLabel( " " + character.getHitP() + "/" + this.character.getMaxHp(), new ImageIcon(res + "hp.png"), JLabel.LEFT);
        hp.setLocation(15, 370);
        hp.setSize(320, 50);
        hp.setFont(this.font);
        hp.setVerticalTextPosition(JLabel.NORTH);

        JLabel artifact = new JLabel("");
        if (character.getArtifact() != null)
            artifact.setIcon( new ImageIcon(res + character.getArtifact().getType() == "attack" ? "artifactA.png" : "artifactD.png") );
        artifact.setLocation(15, 430);
        artifact.setSize(200, 50);
        artifact.setFont(this.font);

        this.stats.put("name", name);
        this.stats.put("type", type);
        this.stats.put("level", level);
        this.stats.put("location", location);
        this.stats.put(Constants.ATTACK_STR, attack);
        this.stats.put(Constants.DEFENSE_STR, defense);
        this.stats.put("hp", hp);
        this.stats.put("exp", exp);
        this.stats.put("artifact", artifact);

        this.add(name);
        this.add(type);
        this.add(level);
        this.add(location);
        this.add(exp);
        this.add(attack);
        this.add(defense);
        this.add(hp);
        this.add(artifact);
    }

    private void loadFont()
    {
        File file = new File("resources/fonts/LeagueGothic-CondensedItalic.otf");
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(30f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        this.font = new Font(Font.SERIF, Font.ITALIC, 40);
    }

    public void setCharacter(Character character) {
        this.character = character;
    }
}
