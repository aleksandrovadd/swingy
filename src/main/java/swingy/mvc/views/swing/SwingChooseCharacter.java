package swingy.mvc.views.swing;

import swingy.bd.DataBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.lang.*;
import java.util.List;

import swingy.mvc.models.characterBuilder.*;
import swingy.mvc.models.Character;
import swingy.util.Constants;

import static swingy.util.Constants.*;

public class SwingChooseCharacter {
    private SwingPanel panel;
    private JTextField inputName;
    private JComboBox characterTypes;
    private JComboBox previousCharacters;
    private SwingStats stats;
    private Character selectedCharacter;
    private CharacterBuilder builder;

    private Map<String, JLabel>     labels;
    private Map<String, JButton>    buttons;
    private Font[]                  fonts;

    public SwingChooseCharacter(SwingPanel panel) {
        this.panel = panel;
        this.inputName = new JTextField("", 5);

        String[] nameTypes = {WARRIOR_TYPE, MAGE_TYPE, ROGUE_TYPE};

        this.characterTypes = new JComboBox<>(nameTypes);
        this.previousCharacters = new JComboBox<>();
        this.builder = new CharacterBuilder();

        this.labels = new HashMap<>();
        labels.put(NAME, new JLabel("Name:"));
        labels.put(OLD, new JLabel("Previously saved characters:"));

        this.buttons = new HashMap<>();
        buttons.put(CREATE, new JButton("Create new character"));
        buttons.put(SELECT_STR, new JButton(Constants.SELECT_STR));
        buttons.put(REMOVE_STR, new JButton(Constants.REMOVE_STR));

        this.fonts = new Font[2];
    }

    public Character ChooseCharacter() throws Exception {
        DataBase.getDb().connectDb();

        this.prepareObjects();
        this.addObjOnFrame();

        panel.revalidate();
        panel.repaint();

        while (true) { if (this.panel.getComponents().length == 0) break; }
        this.panel.revalidate();
        this.panel.repaint();

        return this.selectedCharacter;
    }

    private void    prepareObjects() throws Exception {
        this.loadFonts();
        this.prepareLabels();
        this.prepareNameField();
        this.prepareBoxes();
        this.prepareButtons();
    }

    private void    prepareLabels() {
        labels.get(NAME).setLocation(950, 50);
        labels.get(NAME).setSize(150, 100);
        labels.get(NAME).setFont(fonts[0]);

        labels.get(OLD).setLocation(85, 100);
        labels.get(OLD).setSize(200, 100);
        labels.get(OLD).setFont(fonts[0]);
    }

    private void    prepareNameField() {
        this.inputName.setLocation(900, 125);
        this.inputName.setSize(150, 35);
        this.inputName.setFont(fonts[1]);
    }

    private void    prepareBoxes() throws Exception {
        this.characterTypes.setLocation(898, 175);
        this.characterTypes.setSize(160, 50);
        this.characterTypes.addItemListener (
                (ItemEvent e) -> {
                    this.stats.setCharacter(builder.buildByType( (String) characterTypes.getSelectedItem() ));
                    this.stats.updateData();
                }
        );

        List<String> names = DataBase.getDb().getNames();
        for (String name : names) {
            previousCharacters.addItem(name);
        }
        previousCharacters.setLocation(100, 175);
        previousCharacters.setSize(160, 50);
        previousCharacters.addItemListener(
                (ItemEvent e) -> {
                    try {
                        Character newCharacter = DataBase.getDb().getCharacter( (String) previousCharacters.getSelectedItem() );
                        if (newCharacter == null)
                            newCharacter = builder.buildByType( (String) characterTypes.getSelectedItem() );

                        this.stats.setCharacter(newCharacter);
                        this.stats.updateData();
                    }
                    catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
        );

        this.stats = new SwingStats( names.size() != 0 ? DataBase.getDb().getCharacter((String) previousCharacters.getSelectedItem()) :
                builder.buildByType((String) characterTypes.getSelectedItem()) );
        this.stats.setLocation(400, 400);
    }

    private void    prepareButtons() {
        buttons.get(CREATE).setLocation(875, 250);
        buttons.get(CREATE).setSize(210, 25);
        buttons.get(CREATE).addActionListener( (ActionEvent e) -> this.createNewCharacter() );
        buttons.get(CREATE).setFont(fonts[1]);

        buttons.get(SELECT_STR).setLocation(75, 250);
        buttons.get(SELECT_STR).setSize(100, 25);
        buttons.get(SELECT_STR).addActionListener( (ActionEvent e) -> {
            if (this.previousCharacters.getItemCount() == 0 ) {
                JOptionPane.showMessageDialog(this.panel, "You do not have any character, create him.");
            }
            else {
                this.selectCharacter();
                this.panel.removeAll();
            }
        });
        buttons.get(SELECT_STR).setFont(fonts[1]);

        buttons.get(REMOVE_STR).setLocation(185, 250);
        buttons.get(REMOVE_STR).setSize(100, 25);
        buttons.get(REMOVE_STR).addActionListener( (ActionEvent e) -> this.removeCharacter() );
        buttons.get(REMOVE_STR).setFont(fonts[1]);
    }

    private void    addObjOnFrame() {
        panel.add(labels.get(NAME));
        panel.add(labels.get(OLD));
        panel.add(this.inputName);
        panel.add(this.characterTypes);
        panel.add(this.previousCharacters);
        panel.add(this.stats);
        panel.add(buttons.get(CREATE));
        panel.add(buttons.get(SELECT_STR));
        panel.add(buttons.get(REMOVE_STR));


        labels.get(NAME).repaint();
        labels.get(OLD).repaint();
        inputName.repaint();
        characterTypes.repaint();
        previousCharacters.repaint();
        stats.repaint();
        stats.updateData();
        buttons.get(CREATE).repaint();
        buttons.get(SELECT_STR).repaint();
        buttons.get(REMOVE_STR).repaint();
    }

    private void    loadFonts() {
        File file = new File("resources/fonts/LeagueGothic-CondensedItalic.otf");
//        File file1 = new File("resources/fonts/font3.ttf");
        try {
            this.fonts[0] = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(30f);
//            this.fonts[1] = Font.createFont(Font.TRUETYPE_FONT, file1).deriveFont(20f);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewCharacter() {
        Character newCharacter = builder.buildByType((String)characterTypes.getSelectedItem() );
        String error = builder.setName(newCharacter, inputName.getText() );

        for (int i = 0; i < previousCharacters.getItemCount(); i++) {
            if (previousCharacters.getItemAt(i).equals(inputName.getText())) {
                error = "Character with that name already created";
            }
        }
        if (error != null)
            JOptionPane.showMessageDialog(panel, error);
        else {
            try {
                DataBase.getDb().addNewCharacter(newCharacter);
                previousCharacters.addItem(inputName.getText());
                inputName.setText("");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void removeCharacter() {
        Object removeCharacter = previousCharacters.getSelectedItem();
        try {
            DataBase.getDb().remove((String)removeCharacter);
            previousCharacters.removeItem(removeCharacter);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectCharacter() {
        try {
            this.selectedCharacter = DataBase.getDb().getCharacter((String)this.previousCharacters.getSelectedItem());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}