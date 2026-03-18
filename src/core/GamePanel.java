package core;

import entity.Player;
import ui.TimeUI;
import java.awt.*;
import javax.swing.*;

import map.MapLoader;
import ui.Camera;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private final int FPS = 60;
    private TimeManager timeManager;
    private TimeUI timeUI;

    private int screenWidth = 1920; //เพื่ออิงขนาดจอจากอันนี้ที่เดียว
    private int screenHeight = 1080;

    private Player player;

    private MapLoader mapLoader;
    private Camera camera;

    public void update() {
        camera.update(player);
        // wait logic
    }

    public GamePanel(Player player) {
        this.player = player;

        setPreferredSize(new Dimension(1720, 800));
        setLayout(null);
        setBackground(Color.BLACK);
        setOpaque(true);
        timeManager = new TimeManager();
        timeUI = new TimeUI(timeManager);

        mapLoader = new MapLoader("mapServerRoom");
        camera = new Camera(this,mapLoader);
        player.setCamera(camera);

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

        if (timeUI != null) {
            timeUI.draw(g2);
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