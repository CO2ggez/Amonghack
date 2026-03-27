package ui;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TextBook extends JPanel {
    public int update=0;
    public BufferedImage image;
    public TextBook() throws IOException {
        setPreferredSize(new Dimension(1920, 1080));
        //addKeyListener(new InputManager()); ขออณุญาติย่ายไปสร้างใน gamePanel แทน เนื่องจากระบบเปลี่ยนห้องจะใช้ด้วย

        importImg("/util/asst/textbook_original.png");
        setOpaque(false);

    }
    public void importImg(String path) throws IOException {
        //use this as the prototype for other textbook file using if else Changing textbook file when a certain event
        //happened
        InputStream is = getClass().getResourceAsStream(path);
        image = ImageIO.read(is);
    }
    public void update() throws IOException{
        update+=1;
        if (update==1){
            importImg("/util/asst/textbook_2.png");
        }if(update==2){
            importImg("/util/asst/textbook_3.png");
        }if(update==3){
            importImg("/util/asst/textbook_4.png");
        }if(update==4){
            importImg("/util/asst/textbook_5.png");
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            Graphics2D g2 = (Graphics2D) g;

            // วาดหนังสือ (กลางจอ)
            int w = getWidth();
            int h = getHeight();

            int imgW = w - 300;   // ปรับขนาดได้
            int imgH = h - 200;

            int x = (w - imgW) / 2;
            int y = (h - imgH) / 2;

            g2.drawImage(image, x, y, imgW, imgH, this);
        }
        
    }
}
