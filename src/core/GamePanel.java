package core;

import entity.Player;
import java.awt.*;
import javax.swing.*;
import map.RoomManager;
import ui.Camera;
import ui.DialogBox;
import ui.TimeUI;

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

    private RoomManager roomManager;
    private Camera camera;

    private InputManager inputManager;

    public void update() {
        player.update();
        camera.update(player);
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

        //สร้างแมพและกล้อง และ เชื่อมกล้องกับ player

        roomManager = new RoomManager(player);
        camera = new Camera(this,roomManager);

        inputManager = new InputManager(camera,roomManager,this,player);
        addKeyListener(inputManager);

        player.setCamera(camera);

        gsm = new GameStateManager();
        timeUI = new TimeUI(timeManager, gsm);
        dialogBox = new DialogBox();

        setFocusable(true);


        startGameThread();
        SwingUtilities.invokeLater(() -> { requestFocusInWindow();});
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

        roomManager.drawMap(g,camera);//วาดแมพ

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gsm != null) {
            gsm.draw(g2);
        }

        if (timeUI != null) {
            timeUI.draw(g2);
        }

        player.draw(g2);

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

    public boolean getIsTransitioning() {return isTransitioning;}

    public void setDialogBox(DialogBox dialogBox) {
        this.dialogBox = dialogBox;
    }
}