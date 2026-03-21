package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class DialogBox {
    private BufferedImage img;
    private boolean isVisible = false;
    private String[] currentTexts; //เก็บชุดบท dialog
    private int textIndex = 0;

    public DialogBox() {
        try {
        // เราเปลี่ยนมาใช้pathแบบเต็มกันError
        img = ImageIO.read(getClass().getResourceAsStream("/ui/DialogBox.png"));
        } catch (Exception e) {
            System.out.println("หาไฟล์รูป DialogBox ไม่เจอ");
            e.printStackTrace();
        }
    }

    public void startDialog(String[] texts) {
        this.currentTexts = texts;
        this.textIndex = 0; //เริ่มต้นที่ประโยคแรก
        this.isVisible = true; //สั่งโชว์กล่อง
    }

    public boolean nextText() {
        textIndex++; //ขยับไปบรรทัดต่อไป
        // เช็คว่าประโยคหมดรึยัง
        if (currentTexts == null || textIndex >= currentTexts.length) {
            isVisible = false; //ปิดกล่อง
            return true; //คุยจบ
        }
        return false; //ยังคุยไม่จบ
    }

    public void draw(Graphics2D g) {
        if (!isVisible || currentTexts == null) return;
        //ตั้งค่าตำแหน่งและขนาดกล่อง ให้อยู่ด้านล่างของจอ
        int x = 200;
        int y = 550;
        int width = 1320;
        int height = 200;

        g.drawImage(img, x, y, width, height, null);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 36));
        if (textIndex < currentTexts.length) {
            g.drawString(currentTexts[textIndex], x + 80, y + 100);
        }
    }

    public boolean isVisible() {
        return isVisible;
    }
    // ในไฟล์ DialogBox.java

}