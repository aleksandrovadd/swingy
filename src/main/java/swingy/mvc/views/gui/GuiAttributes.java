package swingy.mvc.views.gui;

import javax.swing.*;
import swingy.mvc.models.Character;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static swingy.util.Constants.*;

public class GuiAttributes extends JPanel {
    private Character character;
    private Map<String, JLabel> stats;
    private Font font;
    private String res = "resources/icons/";

    GuiAttributes(Character character) {
        this.character = character;
        stats = new HashMap<>();

        setLayout(null);
        setSize(325, 500);
        setLocation(50, 50);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawRect(1, 1, 323, 498);
    }

    void notifyData() {
        if (stats.size() == 0) {
            prepareInfo();
        }

        stats.get(NAME).setText("Name: " + (character.getName() == null ? "" : character.getName()) );
        stats.get("type").setText("Type: " + character.getType());

        if (character.getPosition() != null) {
            stats.get("location").setText("Location: [" + character.getPosition().x + ", " + character.getPosition().y + "]");
        }

        stats.get("level").setText("Level: " + character.getLevel());
        stats.get("exp").setText("Exp: " + character.getExp() + "/" + character.getNecessaryExp());
        stats.get("attack").setText(String.valueOf(character.getAttack()));
        stats.get(DEFENSE_STR).setText(" " + character.getDefense());
        stats.get("hp").setText(" " + character.getHitP() + "/" + character.getMaxHp());

        if (character.getArtefact() != null && !character.getArtefact().getType().equals("")) {
            stats.get(ARTEFACT).setIcon(new ImageIcon(
                    res + (character.getArtefact().getType().equals(ATTACK_STR) ? "artefact1.png" : "artefact2.png") ));
            stats.get(ARTEFACT).setText(" " + character.getArtefact().getValue());
        }
        else {
            stats.get(ARTEFACT).setText("");
            stats.get(ARTEFACT).setIcon(null);
        }
    }

    private void prepareInfo() {
        loadFont();

        JLabel name = new JLabel(NAME + ": ");
        name.setLocation(20, 5);
        name.setSize(225, 75);
        name.setFont(font);
        if (character.getName() != null) {
            name.setText(NAME + ": " + character.getName());
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

        JLabel experience = new JLabel("Exp: " + character.getExp() + "/" + character.getNecessaryExp() );
        experience.setLocation(20, 205);
        experience.setSize(320, 50);
        experience.setFont(font);

        JLabel attack = new JLabel( String.valueOf(character.getAttack()), new ImageIcon(res + "sword.png"), JLabel.LEFT);
        attack.setLocation(20, 260);
        attack.setSize(200, 50);
        attack.setFont(font);

        JLabel defense = new JLabel( " " + character.getDefense(), new ImageIcon(res + "shield.png"), JLabel.LEFT);
        defense.setLocation(15, 315);
        defense.setSize(200, 50);
        defense.setFont(font);

        JLabel hitPoints = new JLabel( " " + character.getHitP() + "/" + this.character.getMaxHp(), new ImageIcon(res + "hp.png"), JLabel.LEFT);
        hitPoints.setLocation(15, 370);
        hitPoints.setSize(320, 50);
        hitPoints.setFont(font);
        hitPoints.setVerticalTextPosition(SwingConstants.TOP);

        JLabel artefact = new JLabel("");
        if (character.getArtefact() != null)
            artefact.setIcon( new ImageIcon(res + (character.getArtefact().getType().equals(ATTACK_STR) ? "artefact1.png" : "artefact2.png")));
        artefact.setLocation(15, 430);
        artefact.setSize(200, 50);
        artefact.setFont(font);

        stats.put(NAME, name);
        stats.put("type", type);
        stats.put("level", level);
        stats.put("location", location);
        stats.put(ATTACK_STR, attack);
        stats.put(DEFENSE_STR, defense);
        stats.put("hp", hitPoints);
        stats.put("exp", experience);
        stats.put(ARTEFACT, artefact);

        add(name);
        add(type);
        add(level);
        add(location);
        add(experience);
        add(attack);
        add(defense);
        add(hitPoints);
        add(artefact);
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
