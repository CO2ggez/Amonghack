package core;

import audio.Sound;
import entity.Player;
import event.EventManager;
import event.EventSetup;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import map.RoomManager;
import ui.Camera;
import ui.DialogBox; // นำเข้า Event
import ui.TimeUI;   // นำเข้า Event
import util.AssetLoader;   // นำเข้า AssetLoader


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

    private EventManager eventManager;
    private EventSetup eventSetup;
    private BufferedImage elevatorUI; // ตัวแปรรูปหน้าต่างลิฟต์

    private Sound sound;


    public void update() {
        player.update();
        camera.update(player);
        if (timeManager != null && timeManager.isDayEnded() && !isTransitioning) {
            isTransitioning = true;

            if (sound != null) {
                sound.stopSound("bg1");
            }

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

            if (sound != null) {
                sound.setVolume("bg1", 0.05f);
                sound.loopSound("bg1");
            }

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

        eventManager = new EventManager();
        eventSetup = new EventSetup(eventManager);
        eventSetup.loadZones(); // โหลดจุดคลิกต่างๆ

        // --- โหลดรูปภาพ UI ---
        // **สำคัญ: คุณต้องมีไฟล์รูปภาพลิฟต์ (เช่น elevator_ui.png) ไปใส่ไว้ในโฟลเดอร์ที่ระบุ**
        elevatorUI = AssetLoader.loadImage("/util/asst/ElevatorButton21G.png");

        inputManager = new InputManager(camera,roomManager,this,player);
        addKeyListener(inputManager);

        player.setCamera(camera);

        gsm = new GameStateManager();
        timeUI = new TimeUI(timeManager, gsm);
        dialogBox = new DialogBox();

        sound = new Sound();
        sound.setVolume("bg1", 0.05f);
        sound.loopSound("bg1");

        setFocusable(true);


        startGameThread();
        SwingUtilities.invokeLater(() -> { requestFocusInWindow();});
    }

    public EventManager getEventManager() {
        return eventManager;
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

        // จะวาดตัวละคร Player ก็ต่อเมื่อกล่องข้อความไม่ได้เปิดอยู่
        if (dialogBox == null || !dialogBox.isVisible()) {
            player.draw(g2);
        }

        if (eventManager != null && eventManager.isShowImage()) {
            String activeEvent = eventManager.getActiveZoneName();

            // เช็คว่ากดโดนลิฟต์ และ โหลดรูปมาสำเร็จ
            if (activeEvent.equals("Elevator_Panel") && elevatorUI != null) {
                // คำนวณให้รูปภาพวาดอยู่ตรงกลางหน้าจอพอดี
                int uiX = (getWidth() - elevatorUI.getWidth()) / 2;
                int uiY = (getHeight() - elevatorUI.getHeight()) / 2;
                g2.drawImage(elevatorUI, uiX, uiY, null);
            }
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

    public boolean getIsTransitioning() {return isTransitioning;}

    public void setDialogBox(DialogBox dialogBox) {
        this.dialogBox = dialogBox;
    }

    public Sound getSound() {return sound;}
}