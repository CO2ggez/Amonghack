package core;

import audio.Sound;
import entity.*;
import event.EventManager;
import event.EventSetup;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;
import map.RoomManager;
import network.MinigameManager;
import ui.Camera;
import ui.DialogBox; // นำเข้า Event
import ui.TextBook;
import ui.TimeUI;   // นำเข้า Event
import util.AssetLoader;   // นำเข้า AssetLoader
import util.FontUtil;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private final int FPS = 60;
    public TimeManager timeManager;
    private TimeUI timeUI;
    public DialogBox dialogBox;
    public TextBook textBook;
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


    private float fadeAlpha = 0f;      // 0.0 (ใส) ถึง 1.0 (ดำ)
    private boolean isFading = false;
    private boolean isFadeOut = true;  // true = กำลังดำ, false = กำลังสว่าง
    private Runnable postFadeAction;   // เก็บคำสั่ง "เปลี่ยนชั้น" ไว้ทำตอนจอดำสนิท

    private MinigameManager minigameManager;

    //ข้อความบอกevent ที่กดได้ (check ใน inputmanager)
    private String hintText = null;
    private boolean showHint = false;

    NPCmanager npcmanager;

    //ข้อความแจ้งเตือนหลัง special event เสร็จ
    private String notificationText = null;
    private long notificationStartTime = 0;
    private long notificationDuration = 2000; //วิ
    private float notificationAlpha = 0f;
    private boolean showNotification = false;

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

        //เอาข้อความhint มาจากตำแหน่ง event ที่ player อยู่
        String hint = inputManager.getCurrentHint();
        if (hint != null) {
            hintText = hint;
            showHint = true;
        } else {
            showHint = false;
        }

        npcmanager.updateNPC();

        if (isFading) {
            if (isFadeOut) {
                fadeAlpha += 0.05f;
                if (fadeAlpha >= 1f) {
                    fadeAlpha = 1f;

                    if (postFadeAction != null) {
                        postFadeAction.run();
                        postFadeAction = null;
                    }
                    isFadeOut = false; // เริ่มทำให้จอสว่างคืน
                }
            } else {
                fadeAlpha -= 0.05f;
                if (fadeAlpha <= 0f) {
                    fadeAlpha = 0f;
                    isFading = false;
                }
            }
        }

        //อัพเดทข้อความแจ้งเตือน special event
        if (showNotification) {
            long elapsed = System.currentTimeMillis() - notificationStartTime;

            if (elapsed > notificationDuration) {
                //ค่อยๆจาง
                notificationAlpha -= 0.02f;

                if (notificationAlpha <= 0f) {
                    notificationAlpha = 0f;
                    showNotification = false;
                    notificationText = null;
                }
            }
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
            this.requestFocusInWindow();

            }
        }


    public GamePanel(Player player) {
        this.player = player;

        setPreferredSize(new Dimension(1720, 800));
        setLayout(null);
        setBackground(Color.BLACK);
        setOpaque(true);
        timeManager = new TimeManager();

        // Create TextBook FIRST
        try {
            textBook = new TextBook();
            int textbookWidth = 1100;   // Adjust as needed
            int textbookHeight = 800;  // Adjust as needed
            int x = (1920 - textbookWidth) / 2;   // Center horizontally
            int y = (1080 - textbookHeight) / 2;  // Center vertically
            textBook.setBounds(x, y, textbookWidth, textbookHeight);  // Set position and size (x, y, width, height)
            textBook.setVisible(false);  // Start hidden
            add(textBook);  // Add to panel
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load TextBook image");
        }
        //สร้างแมพและกล้อง และ เชื่อมกล้องกับ player

        roomManager = new RoomManager(player);
        camera = new Camera(this,roomManager);

        eventManager = new EventManager();
        eventSetup = new EventSetup(eventManager);
        eventSetup.loadZones(); // โหลดจุดคลิกต่างๆ

        // --- โหลดรูปภาพ UI ---
        // **สำคัญ: คุณต้องมีไฟล์รูปภาพลิฟต์ (เช่น elevator_ui.png) ไปใส่ไว้ในโฟลเดอร์ที่ระบุ**
        elevatorUI = AssetLoader.loadImage("/util/asst/ElevatorButton21G.png");

        minigameManager = new MinigameManager(this);

        //ตั้งtaskตอนนี้เป็น lan ถ้าไปถึงจุดที่เครื่องอยู่ใน inputmanager กด f แล้วจึงเริ่ม
        minigameManager.setTask("lan");


        player.setCamera(camera);



        // 1. --- เลื่อนการสร้าง npcmanager มาไว้ตรงนี้ก่อน (เพื่อให้มีข้อมูลก่อนส่งไปให้ InputManager) ---
        npcmanager = new NPCmanager(roomManager);

        // 2. --- แก้ไข: เติม npcmanager เข้าไปเป็นตัวแปรที่ 6 (ตัวสุดท้าย) ในวงเล็บ ---
        inputManager = new InputManager(camera, roomManager, this, player, minigameManager, npcmanager,textBook);
        addKeyListener(inputManager);
        player.setCamera(camera);

        gsm = new GameStateManager(this);
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

    public void startTransition(Runnable action) {
        this.postFadeAction = action;
        this.isFading = true;
        this.isFadeOut = true;
        this.fadeAlpha = 0f;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        roomManager.drawMap(g,camera);//วาดแมพ

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //npc
        npcmanager.drawNPC(g, camera.getX());

        if (gsm != null) {
            gsm.draw(g2);
        }

        if (timeUI != null) {
            timeUI.draw(g2);
        }

        // จะวาดตัวละคร Player ก็ต่อเมื่อกล่องข้อความไม่ได้เปิดอยู่
        if (dialogBox == null || !dialogBox.isVisible()) {
            player.draw(g2);
            player.setVisible(isTransitioning);
        }

        g2.setFont(FontUtil.THAI);

        //task text ขวาบน
        g2.setColor(Color.WHITE);
        g2.drawString(minigameManager.taskText, 1450 , 75);

        if (eventManager != null && eventManager.isShowImage()) {
            String activeEvent = eventManager.getActiveZoneName();

            // เช็คว่ากดโดนลิฟต์ และ โหลดรูปมาสำเร็จ
            if (activeEvent.equals("Elevator_Panel") && elevatorUI != null) {
                // ทำฉากด้านหลังมืดลง
                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // คำนวณให้รูปภาพวาดอยู่ตรงกลางหน้าจอพอดี
                int uiX = (getWidth() - elevatorUI.getWidth()) / 2;
                int uiY = (getHeight() - elevatorUI.getHeight()) / 2;
                g2.drawImage(elevatorUI, uiX, uiY, null);
            }
        }

        if (dialogBox != null) {
            dialogBox.draw(g2);
        }

        //วาดข้อความว่า interact event ได้
        if (showHint && hintText != null) {
            int playerScreenX = player.xDelta - camera.getX() + player.offsetX;
            int playerScreenY = player.yDelta - 20;

            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(hintText);
            int h = fm.getHeight();

            // กล่องดำโปร่งใส
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(playerScreenX-10, playerScreenY - h +10, w + 20, h);

            // text
            g2.setColor(Color.WHITE);
            g2.drawString(hintText, playerScreenX , playerScreenY );
        }

        //ทรานซิชั่นถมดำ
        if (fadeAlpha > 0) {
            Graphics2D transition = (Graphics2D) g;
            transition.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            transition.setColor(Color.BLACK);
            transition.fillRect(0, 0, getWidth(), getHeight());
            transition.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // รีเซ็ตค่ากลับ
        }

        //วาด special event
        if (showNotification && notificationText != null) {
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, notificationAlpha));

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(notificationText);

            int x = (getWidth() - textWidth) / 2;
            int y = 250; // top center

            // background
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(x - 20, y - 40, textWidth + 40, 50);

            g2.setColor(Color.YELLOW);
            g2.drawString(notificationText, x, y);

            // reset alpha
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1f));
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

    //function แสดงข้อความ specialevent
    public void showNotification(String text) {
        this.notificationText = text;
        this.notificationStartTime = System.currentTimeMillis();
        this.notificationAlpha = 1f;
        this.showNotification = true;
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

    public boolean isInMinigame() {
        return minigameManager != null && minigameManager.isPlaying();
    }

    public GameStateManager getGSM() {
        return gsm;
    }

    public NPCmanager getNpcmanager() {
        return npcmanager;
    }

    public MinigameManager getMinigameManager() {
        return minigameManager;
    }
}