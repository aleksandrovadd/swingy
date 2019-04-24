package swingy.mvc.views.swing;

import javax.swing.*;
import swingy.mvc.models.Character;
import swingy.util.Constants;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SwingStats extends JPanel {
    private Character character;
    private Map<String, JLabel> stats;
    private Font font;
    private String res = "resources/icons/";

    SwingStats(Character character) {
        this.character = character;
        stats = new HashMap<>();

        setSize(325, 500);
        setLocation(50, 50);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawRect(1, 1, 323, 498);
    }

    void updateData() {
        if (stats.size() == 0) {
            prepareInfo();
        }

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
                    res + (character.getArtifact().getType().equals(Constants.ATTACK_STR) ? "artifactA.png" : "artifactD.png") ));
            stats.get("artifact").setText(" " + character.getArtifact().getValue());
        }
        else {
            stats.get("artifact").setText("");
            stats.get("artifact").setIcon(null);
        }
    }

    private void prepareInfo() {
        loadFont();

        JLabel name = new JLabel("Name: ");
        name.setLocation(20, 5);
        name.setSize(225, 75);
        name.setFont(font);
        if (character.getName() != null) {
            name.setText("Name: " + character.getName());
        }

        JLabel type = new JLabel("Type: " + character.getType());
        type.setLocation(20, 55);
        type.setSize(150, 50);
        type.setFont(font);

        JLabel level = new JLabel("Level: " + character.getLevel());
        level.setLocation(20, 105);
        level.setSize(150, 50);
        level.setFont(font);

        JLabel location = new JLabel("Location: [ ]");
        location.setLocation(20, 155);
        location.setSize(200, 50);
        location.setFont(font);
        if (character.getPosition() != null) {
            location.setText("Location: [" + character.getPosition().x + ", " + character.getPosition().y + "]");
        }

        JLabel exp = new JLabel("Exp: " + character.getExp() + "/" + character.getNecessaryExp() );
        exp.setLocation(20, 205);
        exp.setSize(320, 50);
        exp.setFont(font);

        JLabel attack = new JLabel( String.valueOf(character.getAttack()), new ImageIcon(res + "sword.png"), JLabel.LEFT);
        attack.setLocation(20, 260);
        attack.setSize(200, 50);
        attack.setFont(font);

        JLabel defense = new JLabel( " " + character.getDefense(), new ImageIcon(res + "shield.png"), JLabel.LEFT);
        defense.setLocation(15, 315);
        defense.setSize(200, 50);
        defense.setFont(font);

        JLabel hp = new JLabel( " " + character.getHitP() + "/" + this.character.getMaxHp(), new ImageIcon(res + "hp.png"), JLabel.LEFT);
        hp.setLocation(15, 370);
        hp.setSize(320, 50);
        hp.setFont(font);
        hp.setVerticalTextPosition(SwingConstants.TOP);

        JLabel artifact = new JLabel("");
        if (character.getArtifact() != null)
            artifact.setIcon( new ImageIcon(res + (character.getArtifact().getType().equals(Constants.ATTACK_STR) ? "artifactA.png" : "artifactD.png")));
        artifact.setLocation(15, 430);
        artifact.setSize(200, 50);
        artifact.setFont(font);

        stats.put("name", name);
        stats.put("type", type);
        stats.put("level", level);
        stats.put("location", location);
        stats.put(Constants.ATTACK_STR, attack);
        stats.put(Constants.DEFENSE_STR, defense);
        stats.put("hp", hp);
        stats.put("exp", exp);
        stats.put("artifact", artifact);

        add(name);
        add(type);
        add(level);
        add(location);
        add(exp);
        add(attack);
        add(defense);
        add(hp);
        add(artifact);
    }

    private void loadFont() {
        File file = new File("resources/fonts/LeagueGothic-CondensedItalic.otf");
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(30f);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    void setCharacter(Character character) {
        this.character = character;
    }
}
