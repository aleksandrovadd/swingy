package swingy.mvc.views.swing;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SwingGameLog extends JTextArea {
    private Font font;

    SwingGameLog() {
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
        if (font == null) {
            loadFont();
            setFont(font);
        }

        super.append(str);
        setRows(getRows() + 1);
    }
}