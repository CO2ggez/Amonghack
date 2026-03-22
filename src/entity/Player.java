package entity;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import ui.Camera;

public class Player extends JPanel {
    private int xDelta = 0;
    private int yDelta = 882 - 384; //เอมปรับตำแหน่งตามที่ฝ่าย art คุยกันไว้

    private core.GamePanel panel;

    private Camera camera;

    //Animation
    private Image[] frames;
    private int currentFrame = 0;
    private int totalFrames = 8;
    private boolean checkRight = true;
    boolean moving = false;
    //character
    ImageIcon player = new ImageIcon("src/entity/player.png");
    //เราเพิ่มความเร็วในการเดิน
    private int speed = 7;
    private int speedframe = 5;
    private int aniTick = 0;
    private int aniSpeed = 10;


    public Player() {
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
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

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                //อันนี้เราลองใส่ดูก่อนนะลบได้ เราแค่จะลองทำดูว่าcodeเรามันerrorรึป่าว

                if (e.getKeyCode() == KeyEvent.VK_D) {
                    checkRight = true;
                    xDelta += speed;  // ขวา

                    if (xDelta > camera.getWorldWidth() - 132) { //กับขอบขวา ไม่ให้เดินออก
                        xDelta = camera.getWorldWidth() - 132;
                    }

                    moving = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    checkRight = false;
                    xDelta -= speed;  // ซ้าย

                    if (xDelta < 0) {//เพิ่มกรณีตอนชนขอบแมพ ไม่ให้ทะลุ
                        xDelta = 0;
                    }

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
                moving = false;
            }
        });
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

        int drawX = xDelta - camera.getX();
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

    public int getxDelta() {
        return xDelta;
    }

    public void setPanel(core.GamePanel panel) {
        this.panel = panel;
    }
}
