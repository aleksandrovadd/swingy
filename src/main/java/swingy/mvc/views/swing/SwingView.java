package swingy.mvc.views.swing;

import swingy.mvc.Controller;
import swingy.mvc.views.IView;
import swingy.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwingView extends JFrame implements IView
{
    private Controller controller;
    private KeyAdapter keySupporter;
    private JPanel panel;
    private SwingMapPanel map;
    private JScrollPane scrollMap;
    private JScrollPane scrollGameLog;

    private int squareSize;

    private SwingStats stats;
    private SwingGameLog gameLog;

    private String type;

    public SwingView(Controller controller) {
        super("Swingy");

        setBounds(500, 250, 1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (controller.getCharacter() != null) {
                    controller.saveCharacter();
                }
            }
        });

        this.controller = controller;
        keySupporter = new KeySupporter();
        setVisible(true);
        setFocusable(true);

        panel = new JPanel();
        panel.setBounds(0, 0, 1200, 1000);
        panel.setVisible(true);
        panel.setLayout(null);

        squareSize = 70;

        setContentPane(panel);

        type = Constants.GUI_STR;
        addKeyListener(keySupporter);
    }

    @Override
    public void ChooseCharacter() throws Exception {
        controller.setCharacter(new SwingChooseCharacter(panel).ChooseCharacter());
    }

    @Override
    public void drawGameObjects() {
        initScrolls();
        stats = new SwingStats(controller.getCharacter());
        stats.updateData();

        panel.add(scrollGameLog);
        panel.add(stats);
        panel.add(scrollMap);

        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void viewRepaint() {
        panel.repaint();
    }

    @Override
    public void addLog(String text) {
        gameLog.append(" " + text + "\n");
        scrollGameLog.getViewport().setViewPosition(new Point( scrollGameLog.getViewport().getViewPosition().x,
                scrollGameLog.getViewport().getViewPosition().y + 30) );
    }

    @Override
    public void scrollPositionManager() {
        Point newPosition = new Point(controller.getCharacter().getPosition().x * squareSize - 275,
                controller.getCharacter().getPosition().y * squareSize - 275);

        newPosition.y = newPosition.y <= 0 ? scrollMap.getViewport().getViewPosition().y : newPosition.y;
        newPosition.x = newPosition.x <= 0 ? scrollMap.getViewport().getViewPosition().x : newPosition.x;
        scrollMap.getViewport().setViewPosition(newPosition);
    }

    @Override
    public boolean simpleDialog(String message) {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this, message, "You have a choice", dialogButton);

        return dialogResult == 0;
    }

    @Override
    public void updateData() {
        stats.updateData();
    }

    @Override
    public String getViewType() { return type; }

    @Override
    public void close() {
        setVisible(false);
        dispose();
    }

    private void initScrolls() {
        map = new SwingMapPanel(controller, squareSize);
        scrollMap = new JScrollPane(map);

        scrollMap.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollMap.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollMap.setBounds(500, 50, 650, 650);
        scrollMap.getViewport().setViewPosition(new Point(controller.getCharacter().getPosition().x * 70 - 275,
                controller.getCharacter().getPosition().x * 70 - 275) );
        scrollMap.repaint();

        gameLog = new SwingGameLog();
        scrollGameLog = new JScrollPane(gameLog);
        scrollGameLog.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollGameLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollGameLog.setBounds(50, 565, 325, 400);
        scrollGameLog.repaint();
    }

    private class KeySupporter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (controller.getCharacter() != null) {
                if ((e.getKeyCode() > 36 && e.getKeyCode() < 41)) {
                    controller.keyPressed(e.getKeyCode());
                }
                else if (e.getKeyCode() == 49) {
                    controller.keyPressed(-2);
                }
            }
        }
    }
}