package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import util.FontUtil;

public class DialogBox {
    private BufferedImage img;
    private boolean isVisible = false;
    private String[] currentTexts; //เก็บชุดบท dialog
    private int textIndex = 0;

    // === ตัวแปรสำหรับเก็บรูปภาพตัวละคร ===
    private BufferedImage imgHR, imgBoss, imgJanitor, imgMC, imgIT;

    public DialogBox() {
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/ui/DialogBox.png"));
        } catch (Exception e) {
            System.out.println("หาไฟล์รูป DialogBox ไม่เจอ");
            e.printStackTrace();
        }

        // ใช้ path ตามที่กำหนดมาเป๊ะๆ
        imgHR = loadCharImage("/ui/dialog_characters/HR.png");
        imgBoss = loadCharImage("/ui/dialog_characters/boss.png");
        imgJanitor = loadCharImage("/ui/dialog_characters/janitor.png");
        imgMC = loadCharImage("/ui/dialog_characters/MC.png");
        imgIT = loadCharImage("/ui/dialog_characters/it.png");  
    }

    // === ฟังก์ชันช่วยโหลดรูปและแจ้งเตือน ===
    private BufferedImage loadCharImage(String path) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
            if (image != null) {
                System.out.println("โหลดรูปสำเร็จ: " + path);
            }
            return image;
        } catch (Exception e) {
            System.err.println("หาไฟล์รูปตัวละครไม่เจอ: " + path);
            return null; 
        }
    }

    public void startDialog(String[] texts) {
        this.currentTexts = texts;
        this.textIndex = 0; //เริ่มต้นที่ประโยคแรก
        this.isVisible = true; //สั่งโชว์กล่อง
    }

    public boolean nextText() {
        textIndex++; //ขยับไปบรรทัดต่อไป
        if (currentTexts == null || textIndex >= currentTexts.length) {
            isVisible = false; //ปิดกล่อง
            return true; //คุยจบ
        }
        return false; //ยังคุยไม่จบ
    }

    public void draw(Graphics2D g) {
        if (!isVisible || currentTexts == null) return;

        // 1. วาดกล่องข้อความก่อน (เป็นพื้นหลัง)
        g.drawImage(img, 0, 0, null);

        // 2. วาดรูปตัวละครทับลงบนกล่องข้อความ
        if (textIndex < currentTexts.length) {
            String currentLine = currentTexts[textIndex];
            BufferedImage portraitToDraw = null;

            // เช็คคำขึ้นต้นเพื่อเลือกรูป
            if (currentLine.startsWith("Lauren")) portraitToDraw = imgHR;
            else if (currentLine.startsWith("Momoka:")) portraitToDraw = imgBoss;
            else if (currentLine.startsWith("Shu:")) portraitToDraw = imgJanitor;
            else if (currentLine.startsWith("“Luca:")) portraitToDraw = imgMC;
            else if (currentLine.startsWith("Wei:")) portraitToDraw = imgIT;

            if (portraitToDraw != null) {

                int charX;
                int charY = 1080-(80*6);
                int width = portraitToDraw.getWidth(null);
                int height = portraitToDraw.getHeight(null);

                //if mc วาดแบบ flip
                if (portraitToDraw == imgMC){
                    charX = (6)*6;
                    g.drawImage(portraitToDraw, charX + width, charY, charX, charY + height,
                            0, 0, width, height, null);
                }else{
                    charX = (21+170+64)*6;
                    g.drawImage(portraitToDraw, charX, charY, null);
                }

            }
        }

        // 3. วาดตัวหนังสือเป็นลำดับสุดท้าย
        g.setColor(Color.WHITE);
        g.setFont(FontUtil.THAI);
        if (textIndex < currentTexts.length) {

            int textX = 70*6;
            int textY = 870;

            //แบ่งชื่อกับประโยค
            if (currentTexts[textIndex].contains(":")) {
                String[] parts = currentTexts[textIndex].split(":", 2);

                String name = parts[0].trim();
                String text = parts[1].trim();

                g.drawString(name, textX+30, textY-70);
                g.drawString(text, textX, textY);

            }else{
                g.drawString(currentTexts[textIndex], textX, textY);
            }


        }
    }

    public boolean isVisible() {
        return isVisible;
    }

}