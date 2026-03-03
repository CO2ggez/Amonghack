package core;

import java.awt.*;
import javax.swing.*;
public class GamePanel extends JPanel{
    // ถ้ามาแก้อย่าลบนะ เอาไว้อัพเดตพวกเกมภาพไรงี้
    public void update() {
        //wait logic
    }

    public GamePanel()  {
        this.setBackground(Color.BLACK);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.drawString("game Start",350,300);
    }
}
