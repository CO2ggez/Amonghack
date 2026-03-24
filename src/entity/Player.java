package entity;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import ui.Camera;

public class Player extends JPanel {
    public int xDelta = 0;
    public int yDelta = 882 - 384; //เอมปรับตำแหน่งตามที่ฝ่าย art คุยกันไว้

    public core.GamePanel panel;

    private Camera camera;

    //Animation
    private Image[] frames;
    private Image[] idleFrames; // เปลี่ยนมาใช้ Array เก็บรูปท่ายืนแทน
    private int currentFrame = 0;
    
    private int totalFrames = 8; // จำนวนรูปตอนเดิน
    private int totalIdleFrames = 7;
    
    public boolean checkRight = false;
    public boolean moving = false;

    //เราเพิ่มความเร็วในการเดิน
    private int speed = 7;
    private int aniTick = 0;
    private int aniSpeed = 15;

    //แก้ไม่ให้ตัวละครเดินแล้วดูกระตุก ตอนอยู่กลางกล้อง
    public boolean leftPressed = false;
    public boolean rightPressed = false;

    public Player() {
        //setFocusable(true);
        //setFocusTraversalKeysEnabled(false); ให้ไป focus ตัว inputManager ตัวเดียว
        frames = new Image[totalFrames];
        idleFrames = new Image[totalIdleFrames];

        // โหลด animation ท่ายืนนิ่ง จากโฟลเดอร์ player_idle
        for (int i = 0; i < totalIdleFrames; i++) {
            try {
                idleFrames[i] = new ImageIcon("src/entity/player_idle/" + (i + 1) + ".png").getImage();
            } catch (Exception e) {
                System.out.println("Error โหลดรูปท่ายืนไม่สำเร็จ: /entity/player_idle/" + (i + 1) + ".png");
            }
        }

        // โหลด animation ตัวละคร แบบใช้ path ตรง เพื่อให้เหมือนกัน
        for (int i = 0; i < totalFrames; i++) {
            try {
                frames[i] = new ImageIcon("src/entity/player/" + (i + 1) + ".png").getImage();
            } catch (Exception e) {
                // ถ้าหาไฟล์ไม่เจอ จะได้รู้ว่าพังที่ไฟล์ไหน
                System.out.println("Error โหลดรูปไม่สำเร็จ: /entity/player/" + (i + 1) + ".png");
            }
        }

        //  เอมย้ายไปไว้ใน input manager ที่เดียว เพราะว่าตอนเอมทำแล้วมันไม่โฟกัสตัว inputmanager มาโฟกัสตัวนี้แทน เลยรวมอันนี้เข้าไปใน input manager ด้วยเลยละกันนะ

    }

    //อัพเดทตำแหน่งในนี้แทน เรียก player.update(); ใน gamePanel ที่ method update
    //มันจะได้ขยับตามเวลาใน gameloop แล้วจะ smooth ขึ้น
    public void update() {

        //เพิ่มเช็ค แมพเล็กกว่าขนาดจอ ไม่งั้น player จะเดินออกจอได้ (ติดลบ)
        int maxX = Math.max(0, camera.getWorldWidth() - 140);

        if (rightPressed) {
            xDelta += speed;

            if (xDelta > maxX) {
                xDelta = maxX;
            }
        }

        if (leftPressed) {
            xDelta -= speed;

            if (xDelta < 0) {
                xDelta = 0;
            }
        }
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // ให้ Animation ทำงานตลอดเวลา ไม่ว่าจะเดินหรือยืนนิ่ง
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            currentFrame++;
        }

        //เพิ่มกรณีที่ map เล็กกว่าขนาดจอ ให้วาด player ให้อยุ่ใน map
        int offsetX = 0;

        if (camera.getWorldWidth() < camera.getScreenWidth()) {
            offsetX = (camera.getScreenWidth() - camera.getWorldWidth()) / 2;
            //โดยบวกเพิ่มค่าบริเวณที่ว่างระหว่างแมพกับจอเข้าไปตอนวาด
        }

        int drawX = xDelta - camera.getX() + offsetX;
        int drawY = yDelta;

        // เช็คว่าจะวาดรูปเดินหรือรูปยืนนิ่ง และจัดการเฟรมไม่ให้เกินจำนวนรูปที่มี
        Image currentImg;
        if (moving) {
            if (currentFrame >= totalFrames) currentFrame = 0;
            currentImg = frames[currentFrame];
        } else {
            if (currentFrame >= totalIdleFrames) currentFrame = 0;
            currentImg = idleFrames[currentFrame];
        }
        
        if (currentImg == null) return;

        int originalWidth = currentImg.getWidth(null);
        int originalHeight = currentImg.getHeight(null);

        double scale = 1; //ขออณุญาติเปลี่ยนเปนขนาดที่ art ต้องการ

        int width = (int) (originalWidth * scale);
        int height = (int) (originalHeight * scale);

        Component observer = (panel != null) ? panel : this;

        if (checkRight) {
            g2.drawImage(
                    currentImg,
                    drawX + width,
                    drawY,
                    -width,
                    height,
                    observer
            );
        } else {
            g2.drawImage(
                    currentImg,
                    drawX,
                    drawY,
                    width,
                    height,
                    observer
            );
        }
    }

    public int getxDelta() { //ส่งไปให้กล้องใช้
        return xDelta;
    }

    public void setPanel(core.GamePanel panel) {
        this.panel = panel;
    }

    public void draw(Graphics2D g2) {

        // ให้ Animation ทำงานตลอดเวลา ไม่ว่าจะเดินหรือยืนนิ่ง
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            currentFrame++;
        }

        //เพิ่มกรณีที่ map เล็กกว่าขนาดจอ ให้วาด player ให้อยุ่ใน map
        int offsetX = 0;

        if (camera.getWorldWidth() < camera.getScreenWidth()) {
            offsetX = (camera.getScreenWidth() - camera.getWorldWidth()) / 2;
        }

        int drawX = xDelta - camera.getX() + offsetX;
        int drawY = yDelta;

        // เช็คว่าจะวาดรูปเดินหรือรูปยืนนิ่ง และจัดการเฟรมไม่ให้เกินจำนวนรูปที่มี
        Image currentImg;
        if (moving) {
            if (currentFrame >= totalFrames) currentFrame = 0;
            currentImg = frames[currentFrame];
        } else {
            if (currentFrame >= totalIdleFrames) currentFrame = 0;
            currentImg = idleFrames[currentFrame];
        }
        
        if (currentImg == null) return;

        int originalWidth = currentImg.getWidth(null);
        int originalHeight = currentImg.getHeight(null);

        double scale = 1;

        int width = (int) (originalWidth * scale);
        int height = (int) (originalHeight * scale);

        Component observer = (panel != null) ? panel : this;

        if (checkRight) {
            g2.drawImage(
                    currentImg,
                    drawX + width,
                    drawY,
                    -width,
                    height,
                    observer
            );
        } else {
            g2.drawImage(
                    currentImg,
                    drawX,
                    drawY,
                    width,
                    height,
                    observer
            );
        }
    }
}