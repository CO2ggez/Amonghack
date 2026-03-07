package ui;
import core.InputManager;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class TextBook extends JPanel {
    private BufferedImage img;
    public TextBook() throws IOException {
        setSize(100,100);
        addKeyListener(new InputManager());
        importImg();

    }
    public void importImg() throws IOException {
        InputStream is = getClass().getResourceAsStream("Textbook.webp");
        img = ImageIO.read(is);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }
}
