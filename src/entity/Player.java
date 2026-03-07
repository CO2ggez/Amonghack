package entity;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
public class Player extends JPanel{
    private int xDelta = 0;
    private int yDelta = 520;


    //Animation
    private Image[] frames;
    private int currentFrame = 0;
    private int totalFrames = 4;
    private boolean checkLeft = false;
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


                if (e.getKeyCode() == KeyEvent.VK_A) {
                    checkLeft = true;
                    xDelta -= speed;  // ซ้าย
                    moving = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_D) {
                    checkLeft = false;
                    xDelta += speed;  // ขวา
                    moving = true;
                }

                //ถ้ายุเฟรมสุดท้ายแล้วจะไปเริ่มใหม่
                if (moving) {
                    currentFrame++;
                    if (currentFrame >= totalFrames){
                        currentFrame =0;
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
        //g.fillRect(100+xDelta,100,200,50);

        //อันนี้ใส่พื้นลองใครบางคนทำgravityให้นะจ๊ะ
        //gravity ไม่น่าต้องใช้นะ-v
        Graphics2D g2 = (Graphics2D) g;

        int drawX = 100 + xDelta;
        int drawY = 100 + yDelta;
        int width = 80;
        int height = 80;

        //อันนี้เอาไว้กลับรูปเวลาเดินไปอีกด้าน
        if (checkLeft) {
            g2.drawImage(
                    frames[currentFrame],
                    drawX + width,
                    drawY,
                    -width,
                    height,
                    this
            );
        } else {
            g2.drawImage(
                    frames[currentFrame],
                    drawX,
                    drawY,
                    width,
                    height,
                    this
            );
        }
    }
}
