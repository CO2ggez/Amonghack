package core;

import entity.Player;
import java.awt.*;
import javax.swing.*;
import ui.DialogBox;
import ui.TimeUI;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private final int FPS = 60;
    public TimeManager timeManager;
    private TimeUI timeUI;
    public DialogBox dialogBox;

    public void update() {
        // wait logic
    }

    public GamePanel(Player player) {
        setPreferredSize(new Dimension(1720, 800));
        setLayout(null);
        setBackground(Color.BLACK);
        setOpaque(true);
        timeManager = new TimeManager();
        timeUI = new TimeUI(timeManager);
        dialogBox = new DialogBox();
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


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(64, 64, 64, 150/*ปรับความโปร่งใส*/)); //พื้น
        g2.fillRect(0, 700, getWidth(), 100);

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
}