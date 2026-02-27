package core;

import ui.TimeUI;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable {

    private Thread gameThread;
    private TimeManager timeManager;
    private TimeUI timeUI;

    public GamePanel() {
        this.setBackground(Color.BLACK); // พื้นหลังดำ
        timeManager = new TimeManager();
        timeUI = new TimeUI(timeManager);
    }

    public void startGameThread() {
        timeManager.start();
        gameThread = new Thread(this, "GameScreenThread");
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) {
            repaint();
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        timeUI.draw(g2);
    }
}