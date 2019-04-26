package swingy.mvc.views.swing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import swingy.mvc.Controller;
import swingy.mvc.models.Monster;

public class SwingMapPanel extends JPanel {
    private Controller controller;
    private int sizeSquare;

    SwingMapPanel(Controller controller, int sizeSquare) {
        this.controller = controller;
        this.sizeSquare = sizeSquare;
        setLayout(null);
        setDoubleBuffered(true);
    }

    @Override
    public void  paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D)g;

        drawMap(g2);
        drawCharacter(g2);
        drawEnemies(g2);
    }

    private void drawMap(Graphics2D g2) {
        setPreferredSize( new Dimension(sizeSquare * controller.getSizeMap(), sizeSquare * controller.getSizeMap()) );
        for (int i = 0; i < controller.getSizeMap(); i++) {
            for (int j = 0; j < controller.getSizeMap(); j++) {
                g2.drawRect(sizeSquare * j, sizeSquare * i, sizeSquare, sizeSquare);
            }
        }
    }

    private void drawCharacter(Graphics2D g2) {
        Image img = getToolkit().getImage("resources/characters/" + controller.getCharacter().getType() + ".png");
        prepareImage(img, this);

        g2.setColor(new Color(101, 255, 0, 75));
        g2.fillRect(controller.getCharacter().getPosition().x * sizeSquare, controller.getCharacter().getPosition().y * sizeSquare, sizeSquare, sizeSquare);
        g2.drawImage(img, controller.getCharacter().getPosition().x * sizeSquare + (sizeSquare / 4), controller.getCharacter().getPosition().y * sizeSquare, this);
    }

    private void drawEnemies(Graphics2D g2) {
        ArrayList<Monster> monsters = controller.getMonsters();
        Image myimg;

        for (Monster monster : monsters) {
            myimg = getToolkit().getImage("resources/characters/monster" + monster.getNumImg() + ".png");

            prepareImage(myimg, this);
            g2.drawImage(myimg, monster.getPosition().x * sizeSquare + (sizeSquare  / 4), monster.getPosition().y * sizeSquare, this);
        }
    }
}