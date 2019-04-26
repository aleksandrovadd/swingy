package swingy.mvc.views.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import swingy.mvc.Controller;
import swingy.mvc.models.Monster;

public class GuiMapPanel extends JPanel {
    private Controller controller;
    private int sizeSquare;

    GuiMapPanel(Controller controller, int sizeSquare) {
        this.controller = controller;
        this.sizeSquare = sizeSquare;
        setLayout(null);
        setDoubleBuffered(true);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics;

        drawMap(graphics2D);
        drawCharacter(graphics2D);
        drawEnemies(graphics2D);
    }

    private void drawMap(Graphics2D g2) {
        setPreferredSize(new Dimension(sizeSquare * controller.getSizeMap(), sizeSquare * controller.getSizeMap()));
        for (int i = 0; i < controller.getSizeMap(); i++) {
            for (int k = 0; k < controller.getSizeMap(); k++) {
                g2.drawRect(sizeSquare * k, sizeSquare * i, sizeSquare, sizeSquare);
            }
        }
    }

    private void drawCharacter(Graphics2D g2) {
        Image img = getToolkit().getImage("resources/characters/" + controller.getCharacter().getType() + ".png");
        prepareImage(img, this);

        g2.setColor(new Color(100, 254, 1, 75));
        g2.fillRect(controller.getCharacter().getPosition().x * sizeSquare, controller.getCharacter().getPosition().y * sizeSquare, sizeSquare, sizeSquare);
        g2.drawImage(img, controller.getCharacter().getPosition().x * sizeSquare + (sizeSquare / 4), controller.getCharacter().getPosition().y * sizeSquare, this);
    }

    private void drawEnemies(Graphics2D g2) {
        ArrayList<Monster> monsters = controller.getMonsters();

        for (Monster monster : monsters) {
            Image monsterImage = getToolkit().getImage("resources/characters/monster" + monster.getNumImg() + ".png");

            prepareImage(monsterImage, this);
            g2.drawImage(monsterImage, monster.getPosition().x * sizeSquare + (sizeSquare  / 4), monster.getPosition().y * sizeSquare, this);
        }
    }
}