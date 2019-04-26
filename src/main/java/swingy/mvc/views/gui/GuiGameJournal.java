package swingy.mvc.views.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GuiGameJournal extends JTextArea {
    private Font font;

    GuiGameJournal() {
        setLayout(null);
        setAutoscrolls(true);
        setBackground(Color.pink);
        setForeground(new Color(84, 10, 143));

        setEditable(false);
        setFocusable(false);
    }

    private void loadFont() {
        try {
            File file = new File("resources/fonts/RobotoCondensed-Italic.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(18f);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void	append(String str) {
        super.append(str);
        if (font == null) {
            loadFont();
            setFont(font);
        }

        setRows(getRows() + 1);
    }
}