package entity;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import ui.Camera;

public class Player extends JPanel {
    public int xDelta = 0;
    private int yDelta = 882 - 384; //เอมปรับตำแหน่งตามที่ฝ่าย art คุยกันไว้

    public core.GamePanel panel;

    private Camera camera;

    //Animation
    private Image[] frames;
    private int currentFrame = 0;
    private int totalFrames = 8;
    public boolean checkRight = true;
    public boolean moving = false;
    //character
    ImageIcon player = new ImageIcon("src/entity/player.png");
    //เราเพิ่มความเร็วในการเดิน
    private int speed = 7;
    private int speedframe = 5;
    private int aniTick = 0;
    private int aniSpeed = 10;

    //แก้ไม่ให้ตัวละครเดินแล้วดูกระตุก ตอนอยู่กลางกล้อง
    public boolean leftPressed = false;
    public boolean rightPressed = false;

    public Player() {
        //setFocusable(true);
        //setFocusTraversalKeysEnabled(false); ให้ไป focus ตัว inputManager ตัวเดียว
        frames = new Image[totalFrames];

        // โหลด animation ตัวละคร
        for (int i = 0; i < totalFrames; i++) {
            try {
                frames[i] = new ImageIcon(getClass().getResource("/entity/player/" + (i + 1) + ".png")).getImage();
            } catch (Exception e) {
                // ถ้าหาไฟล์ไม่เจอ จะได้รู้ว่าพังที่ไฟล์ไหน
                System.out.println("Error โหลดรูปไม่สำเร็จ: /entity/player/" + (i + 1) + ".png");
            }
        }

        //  เอมย้ายไปไว้ใน input manager ที่เดียว เพราะว่าตอนเอมทำแล้วมันไม่โฟกัสตัว inputmanager มาโฟกัสตัวนี้แทน เลยรวมอันนี้เข้าไปใน input manager ด้วยเลยละกันนะ
        /*
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                //อันนี้เราลองใส่ดูก่อนนะลบได้ เราแค่จะลองทำดูว่าcodeเรามันerrorรึป่าว

                if (e.getKeyCode() == KeyEvent.VK_D) {
                    checkRight = true;
                    rightPressed = true; //เปลี่ยนไปเพิ่ม xDelta ใน update ที่จะเรียกใช้ตลอดแทน

                    moving = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    checkRight = false;
                    leftPressed = true;

                    moving = true;
                }


                //DIALOGUE นะจ๊ะ
                if (e.getKeyCode() == KeyEvent.VK_E) {
                    if (!panel.dialogBox.isVisible()) {
                        String[] bossText = {
                                "สวัสดีพนักงานใหม่...",
                                "งานของนายวันนี้คือไปซ่อมเซิร์ฟเวอร์ซะ",
                                "ลาออกไปซะ"
                        };
                        panel.dialogBox.startDialog(bossText);
                        panel.timeManager.setPaused(true);

                    }
                }

                repaint();
                //end
            }

            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_D) {
                    rightPressed = false;
                }

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    leftPressed = false;
                }

                moving = leftPressed || rightPressed; //เปลี่ยนค่าทุกตัวแปลให้รุ้ว่าไม่ได้ขยับ

            }
        });
        */
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.fillRect(100+xDelta,100,200,50);

        //อันนี้ใส่พื้นลองใครบางคนทำgravityให้นะจ๊ะ
        //gravity ไม่น่าต้องใช้นะ-v
        Graphics2D g2 = (Graphics2D) g;

        if (moving) {
            aniTick++;
            if (aniTick >= aniSpeed) {
                aniTick = 0;
                currentFrame++;
                if (currentFrame >= totalFrames) {
                    currentFrame = 0;
                }
            }
        } else {
            currentFrame = 0;
        }

        //เพิ่มกรณีที่ map เล็กกว่าขนาดจอ ให้วาด player ให้อยุ่ใน map
        int offsetX = 0;

        if (camera.getWorldWidth() < camera.getScreenWidth()) {
            offsetX = (camera.getScreenWidth() - camera.getWorldWidth()) / 2;
            //โดยบวกเพิ่มค่าบริเวณที่ว่างระหว่างแมพกับจอเข้าไปตอนวาด
        }

        int drawX = xDelta - camera.getX() + offsetX;
        int drawY = yDelta;

        Image currentImg = frames[currentFrame];
        if (currentImg == null) return;

        int originalWidth = currentImg.getWidth(null);
        int originalHeight = currentImg.getHeight(null);

        double scale = 1; //ขออณุญาติเปลี่ยนเปนขนาดที่ art ต้องการ

        int width = (int) (originalWidth * scale);
        int height = (int) (originalHeight * scale);

        if (checkRight) {
            g2.drawImage(
                    currentImg,
                    drawX + width,
                    drawY,
                    -width,
                    height,
                    this
            );
        } else {
            g2.drawImage(
                    currentImg,
                    drawX,
                    drawY,
                    width,
                    height,
                    this
            );
        }
    }

    public int getxDelta() { //ส่งไปให้กล้องใช้
        return xDelta;
    }

    public void setPanel(core.GamePanel panel) {
        this.panel = panel;
    }
}
