package core;

import entity.Player;
import ui.TimeUI;
import java.awt.*;
import javax.swing.*;
import ui.DialogBox;

import map.MapLoader;
import ui.Camera;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private final int FPS = 60;
    public TimeManager timeManager;
    private TimeUI timeUI;
    public DialogBox dialogBox;
    private GameStateManager gsm;
    private boolean isTransitioning = false;

    private int screenWidth = 1920; //เพื่ออิงขนาดจอจากอันนี้ที่เดียว
    private int screenHeight = 1080;

    private Player player;

    private MapLoader mapLoader;
    private Camera camera;

    public void update() {
        camera.update(player);
        // wait logic
        if (timeManager != null && timeManager.isDayEnded() && !isTransitioning) {
            isTransitioning = true;
            // สั่งหยุดเวลาไว้ก่อน
            timeManager.setPaused(true);
            this.requestFocusInWindow();
        }
        if (gsm != null && !isTransitioning) {
            gsm.update();
        }
    }

    public void startNextDay() {
        if (isTransitioning) {
            //บอก GameStateManager ให้ขยับไปวันถัดไป
            gsm.nextDay();

            //รีเซ็ตเวลาใน TimeManager กลับไป 00:00
            timeManager.resetDay();
            timeManager.setPaused(false); // ให้เวลาเดินต่อ
            isTransitioning = false;
            //ปิดสถานะจอดำ
            for (Component c : getComponents()) {
                if (c instanceof Player) {
                    c.requestFocusInWindow();
                    break;
                }
            }
        }
    }

    public GamePanel(Player player) {
        this.player = player;

        setPreferredSize(new Dimension(1720, 800));
        setLayout(null);
        setBackground(Color.BLACK);
        setOpaque(true);
        timeManager = new TimeManager();

        mapLoader = new MapLoader("mapServerRoom");
        camera = new Camera(this,mapLoader);
        player.setCamera(camera);

        gsm = new GameStateManager();
        timeUI = new TimeUI(timeManager, gsm);
        dialogBox = new DialogBox();

        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                // แค่เช็คว่าติดสถานะจอดำอยู่มั้ย ถ้าใช่ กดปุ่มไหนก็ทำงานเลย
                if (isTransitioning) {
                    startNextDay();
                }
            }
        });
        startGameThread();
    }

    private void startGameThread() {
        if (timeManager != null) {
            timeManager.start();
        }
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        mapLoader.drawMap(g, camera);//วาดแมพ

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gsm != null) {
            gsm.draw(g2);
        }

        if (timeUI != null) {
            timeUI.draw(g2);
        }
        if (dialogBox != null) {
            dialogBox.draw(g2);
        }
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }
}