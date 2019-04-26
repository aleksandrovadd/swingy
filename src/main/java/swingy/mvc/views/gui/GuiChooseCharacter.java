package swingy.mvc.views.gui;

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

import swingy.mvc.models.CharacterBuilder;
import swingy.mvc.models.Character;
import swingy.util.Constants;

import static swingy.util.Constants.*;

class GuiChooseCharacter {

    private JPanel panel;
    private JTextField inputName;
    private JComboBox characterTypes;
    private JComboBox<String> previousCharacters;
    private GuiAttributes attributes;
    private Character selectedCharacter;

    private Map<String, JLabel> labels;
    private Map<String, JButton> buttons;
    private Font[] fonts;

    GuiChooseCharacter(JPanel panel) {
        this.panel = panel;
        inputName = new JTextField(EMPTY_STRING, 5);

        String[] nameTypes = { WARRIOR_TYPE, MAGE_TYPE, ROGUE_TYPE };

        characterTypes = new JComboBox<>(nameTypes);
        previousCharacters = new JComboBox<>();

        labels = new HashMap<>();
        labels.put(NAME, new JLabel("Name:"));
        labels.put(OLD, new JLabel("Previously saved characters:"));

        buttons = new HashMap<>();
        buttons.put(CREATE, new JButton("Create new character"));
        buttons.put(SELECT_STR, new JButton(Constants.SELECT_STR));
        buttons.put(REMOVE_STR, new JButton(Constants.REMOVE_STR));

        fonts = new Font[2];
    }

    Character ChooseCharacter() throws Exception {
        DataBase.getInstance().connectDatabase();

        setupObjects();
        addObjOnFrame();

        panel.revalidate();
        panel.repaint();

        while (true) {
            if (panel.getComponents().length == 0) {
                break;
            }
        }
        panel.revalidate();
        panel.repaint();

        return selectedCharacter;
    }

    private void setupObjects() throws Exception {
        loadFonts();
        setupLabels();
        setupNameField();
        setupBoxes();
        prepareButtons();
    }

    private void setupLabels() {
        labels.get(NAME).setLocation(950, 50);
        labels.get(NAME).setSize(150, 100);
        labels.get(NAME).setFont(fonts[0]);

        labels.get(OLD).setLocation(85, 100);
        labels.get(OLD).setSize(200, 100);
        labels.get(OLD).setFont(fonts[0]);
    }

    private void setupNameField() {
        inputName.setLocation(900, 125);
        inputName.setSize(150, 35);
        inputName.setFont(fonts[1]);
    }

    private void setupBoxes() throws Exception {
        characterTypes.setLocation(898, 175);
        characterTypes.setSize(160, 50);
        characterTypes.addItemListener (
                (ItemEvent e) -> {
                    if (characterTypes.getSelectedItem() != null) {
                        attributes.setCharacter(CharacterBuilder.buildByType((String) characterTypes.getSelectedItem()));
                        attributes.notifyData();
                    }
                }
        );

        List<String> names = DataBase.getInstance().getNames();
        for (String name : names) {
            previousCharacters.addItem(name);
        }
        previousCharacters.setLocation(100, 175);
        previousCharacters.setSize(160, 50);
        previousCharacters.addItemListener(
                (ItemEvent e) -> {
                    try {
                        Character newCharacter = DataBase.getInstance().selectCharacter((String) previousCharacters.getSelectedItem() );
                        if (newCharacter == null) {
                            newCharacter = CharacterBuilder.buildByType((String) characterTypes.getSelectedItem());
                        }

                        attributes.setCharacter(newCharacter);
                        attributes.notifyData();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
        );

        attributes = new GuiAttributes(names.size() != 0 ? DataBase.getInstance().selectCharacter((String) previousCharacters.getSelectedItem()) :
                CharacterBuilder.buildByType((String) characterTypes.getSelectedItem()) );
        attributes.setLocation(400, 400);
    }

    private void prepareButtons() {
        buttons.get(CREATE).setLocation(875, 250);
        buttons.get(CREATE).setSize(210, 25);
        buttons.get(CREATE).addActionListener( (ActionEvent e) -> this.createNewCharacter());
        buttons.get(CREATE).setFont(fonts[1]);

        buttons.get(SELECT_STR).setLocation(75, 250);
        buttons.get(SELECT_STR).setSize(100, 25);
        buttons.get(SELECT_STR).addActionListener( (ActionEvent e) -> {
            if (previousCharacters.getItemCount() == 0) {
                JOptionPane.showMessageDialog(panel, "You do not have any character, create him.");
            } else {
                selectCharacter();
                panel.removeAll();
            }
        });
        buttons.get(SELECT_STR).setFont(fonts[1]);

        buttons.get(REMOVE_STR).setLocation(185, 250);
        buttons.get(REMOVE_STR).setSize(100, 25);
        buttons.get(REMOVE_STR).addActionListener( (ActionEvent e) -> removeCharacter() );
        buttons.get(REMOVE_STR).setFont(fonts[1]);
    }

    private void addObjOnFrame() {
        panel.add(labels.get(NAME));
        panel.add(labels.get(OLD));
        panel.add(inputName);
        panel.add(characterTypes);
        panel.add(previousCharacters);
        panel.add(attributes);
        panel.add(buttons.get(CREATE));
        panel.add(buttons.get(SELECT_STR));
        panel.add(buttons.get(REMOVE_STR));


        labels.get(NAME).repaint();
        labels.get(OLD).repaint();
        inputName.repaint();
        characterTypes.repaint();
        previousCharacters.repaint();
        attributes.repaint();
        attributes.notifyData();
        buttons.get(CREATE).repaint();
        buttons.get(SELECT_STR).repaint();
        buttons.get(REMOVE_STR).repaint();
    }

    private void loadFonts() {
        File file = new File("resources/fonts/LeagueGothic-CondensedItalic.otf");
        try {
            fonts[0] = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(30f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewCharacter() {
        if (characterTypes.getSelectedItem() != null) {
            Character newCharacter = CharacterBuilder.buildByType((String) characterTypes.getSelectedItem());
            String error = CharacterBuilder.setName(newCharacter, inputName.getText());

            for (int i = 0; i < previousCharacters.getItemCount(); i++) {
                if (previousCharacters.getItemAt(i).equals(inputName.getText())) {
                    error = "Character with that name already created";
                }
            }
            if (error != null)
                JOptionPane.showMessageDialog(panel, error);
            else {
                try {
                    DataBase.getInstance().insertCharacter(newCharacter);
                    previousCharacters.addItem(inputName.getText());
                    inputName.setText(EMPTY_STRING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void removeCharacter() {
        Object removeCharacter = previousCharacters.getSelectedItem();
        try {
            DataBase.getInstance().deleteCharacter((String)removeCharacter);
            previousCharacters.removeItem(removeCharacter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectCharacter() {
        try {
            selectedCharacter = DataBase.getInstance().selectCharacter((String) previousCharacters.getSelectedItem());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}