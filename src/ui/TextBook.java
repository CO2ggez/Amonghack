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
    public BufferedImage image;
    public TextBook() throws IOException {
        setSize(100,100);
        //addKeyListener(new InputManager()); ขออณุญาติย่ายไปสร้างใน gamePanel แทน เนื่องจากระบบเปลี่ยนห้องจะใช้ด้วย

        importImg();

    }
    public void importImg() throws IOException {
        InputStream is = getClass().getResourceAsStream("/ui/Textbook.webp");
        image = ImageIO.read(is);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the image if it exists
        if (image != null) {
            // Draw the image scaled to fit the panel
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Optional: Draw placeholder if image failed to load
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawString("TextBook Image", 10, getHeight() / 2);
        }
    }

}
