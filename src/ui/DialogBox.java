package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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

        // 👉 ใช้ขนาดจริงของรูป
        int x = (1920 - img.getWidth()) / 2;
        int y = 1080 - img.getHeight() - 50;

        // วาดกล่อง
        g.drawImage(img, x, y, null);

        // ตั้งค่าฟอนต์
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 36));

        if (textIndex < currentTexts.length) {

            // 👉 คำนวณตำแหน่งข้อความให้ "อยู่ในกล่องจริง"
            FontMetrics fm = g.getFontMetrics();
            //ตำแหน่งของข้อความ dialog
            int textX = x + 420;                  // ขยับซ้าย-ขวา
            int textY = y + img.getHeight() - 190; // ขึ้น-ลง

            g.drawString(currentTexts[textIndex], textX, textY);
        }
    }

    public boolean isVisible() {
        return isVisible;
    }
    // ในไฟล์ DialogBox.java

}