package entity;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
public class Player extends JPanel{
    private int xDelta = 0;
    private int yDelta = 520;

    private core.GamePanel panel;


    //Animation
    private Image[] frames;
    private int currentFrame = 0;
    private int totalFrames = 8;
    private boolean checkRight = false;
    boolean moving = false;
    //character
    ImageIcon player = new ImageIcon("src/entity/player.png");
    //เราเพิ่มความเร็วในการเดิน
    private int speed = 5;



    public Player(){
        //โหลดanimationขิงตัวละคร
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        frames = new Image[totalFrames];
        for (int i = 0; i < totalFrames; i++) {
            frames[i] = new ImageIcon(
                    getClass().getResource("player/" + (i + 1) + ".png")
            ).getImage();
        }

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                //อันนี้เราลองใส่ดูก่อนนะลบได้ เราแค่จะลองทำดูว่าcodeเรามันerrorรึป่าว
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    yDelta -= speed;  // ขึ้น ทำกระโดดไม่เป็น
                    moving = true;
                }


                if (e.getKeyCode() == KeyEvent.VK_D) {
                    checkRight = true;
                    xDelta += speed;  // ขวา
                    moving = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    checkRight = false;
                    xDelta -= speed;  // ซ้าย
                    moving = true;
                }

                //ถ้ายุเฟรมสุดท้ายแล้วจะไปเริ่มใหม่
                if (moving) {
                    currentFrame++;
                    if (currentFrame >= totalFrames){
                        currentFrame =0;
                    }
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
                            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (panel.dialogBox.isVisible()) {
                    boolean isFinished = panel.dialogBox.nextText();
                    if (isFinished) {
                        panel.timeManager.setPaused(false); // คุยจบแล้วให้เวลาเดินต่อ
                    }
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
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int drawX = 100 + xDelta;
        int drawY = 100 + yDelta;

        Image currentImg = frames[currentFrame];
        int originalWidth = currentImg.getWidth(null);
        int originalHeight = currentImg.getHeight(null);

        // กำหนดตัวคูณขนาด (Scale) เพื่อให้ใหญ่ขึ้น
        double scale = 0.5; 

        int width = (int) (originalWidth * scale);
        int height = (int) (originalHeight * scale);

        // อันนี้เอาไว้กลับรูปเวลาเดินไปอีกด้าน
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
}
