package core;

import audio.Sound;
import entity.*;
import event.EventManager;
import event.EventSetup;
import event.TriggerZone;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;
import map.RoomManager;
import network.MinigameManager;
import ui.Camera;
import ui.CgLoader;
import ui.DialogBox;
import ui.Ending;
import ui.TextBook;
import ui.TimeUI;
import util.AssetLoader;
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

    private int screenWidth = 1920;
    private int screenHeight = 1080;

    private Player player;
    private RoomManager roomManager;
    private Camera camera;
    private InputManager inputManager;
    private EventManager eventManager;
    private EventSetup eventSetup;
    private BufferedImage elevatorUI;
    private Sound sound;

    private float fadeAlpha = 0f;
    private boolean isFading = false;
    private boolean isFadeOut = true;
    private Runnable postFadeAction;
    private MinigameManager minigameManager;

    private String hintText = null;
    private boolean showHint = false;

    NPCmanager npcmanager;

    private String notificationText = null;
    private long notificationStartTime = 0;
    private long notificationDuration = 2000;
    private float notificationAlpha = 0f;
    private boolean showNotification = false;

    public CgLoader cgLoader;
    public Ending gameEnding;
    public boolean showingEnding = false;
    //เช็คไฟดับ
    private boolean isLightOut = false;
    public boolean finishedObjective = false;

    public void update() {
        if (showingEnding) {

            if (gsm != null && gsm.getCurrentDay() == 5) {
                if (gameEnding != null && gameEnding.isFinished()) {
                    gsm.playNextEnding();
                }
                return;
            }
        }

        player.update();
        camera.update(player);

        if (timeManager != null && timeManager.isDayEnded() && !isTransitioning) {
            isTransitioning = true;

            if (sound != null) {
                sound.stopSound("bg1");
            }

            timeManager.setPaused(true);

            // เช็คเงื่อนไขหมดเวลาของวันที่ 4 และ วันที่ 5
            if (gsm != null) {
                /*if (gsm.getCurrentDay() == 4 && minigameManager.score < 8) {
                    isTransitioning = false;
                    showingEnding = true;
                    gameEnding.startEnding("CG-gameover");
                } else */if (gsm.getCurrentDay() == 5) {
                    isTransitioning = false;
                    gsm.checkEndGame();
                } else {
                    this.requestFocusInWindow();
                }
            }
        }

        if (gsm != null && !isTransitioning) {
            gsm.update();
        }

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
                    isFadeOut = false;
                }
            } else {
                fadeAlpha -= 0.05f;
                if (fadeAlpha <= 0f) {
                    fadeAlpha = 0f;
                    isFading = false;
                }
            }
        }

        if (showNotification) {
            long elapsed = System.currentTimeMillis() - notificationStartTime;
            if (elapsed > notificationDuration) {
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
            gsm.nextDay();
            timeManager.resetDay();
            timeManager.setPaused(false);
            isTransitioning = false;

            if (sound != null) {
                sound.setVolume("bg1", 0.05f);
                sound.loopSound("bg1");
            }
            this.requestFocusInWindow();
        }
    }

    public GamePanel(Player player) {
        this.player = player;
        setPreferredSize(new Dimension(1720, 800));

        setPreferredSize(new Dimension(getScreenWidth(), getScreenHeight()));
        setLayout(null);
        setBackground(Color.BLACK);
        setOpaque(true);
        timeManager = new TimeManager();

        cgLoader = new CgLoader();
        gameEnding = new Ending(cgLoader);

        try {
            textBook = new TextBook();
            textBook.setBounds(0, 0, screenWidth, screenHeight);
            textBook.setVisible(false);
            add(textBook);
            setComponentZOrder(textBook, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        roomManager = new RoomManager(player);
        camera = new Camera(this,roomManager);
        eventManager = new EventManager();
        eventSetup = new EventSetup(eventManager);
        eventSetup.loadZones();
        elevatorUI = AssetLoader.loadImage("/util/asst/ElevatorButton21G.png");
        minigameManager = new MinigameManager(this);
        player.setCamera(camera);
        npcmanager = new NPCmanager(roomManager);
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

    public EventManager getEventManager() { return eventManager; }

    private boolean shouldShowElevatorGuide() {
        if (roomManager == null || eventManager == null || player == null) return false;
        String room = roomManager.getCurrentRoomName();
        if (room == null || !room.startsWith("lift")) return false;
        if (eventManager.isShowImage()) return false;
        TriggerZone zone = eventManager.getZoneByName("Elevator_Panel");
        if (zone == null) return false;
        return Math.abs(player.xDelta - zone.getCenterX()) <= 350;
    }

    private void drawElevatorGuide(Graphics2D g2) {
        TriggerZone zone = eventManager.getZoneByName("Elevator_Panel");
        if (zone == null) return;
        int targetX = zone.getCenterX() - camera.getX() - 50;
        int targetY = zone.getY() + 100;
        int bob = (int) (Math.sin(System.currentTimeMillis() / 120.0) * 8);
        if (targetX < -100 || targetX > getWidth() + 100) return;
        String text = "คลิกตรงนี้เพื่อใช้งานลิฟต์";
        Font oldFont = g2.getFont();
        g2.setFont(FontUtil.THAI.deriveFont(Font.BOLD, 24f));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int boxX = targetX - (textWidth / 2) - 18;
        int boxY = targetY - 110 + bob;
        int boxW = textWidth + 36;
        int boxH = 42;
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawString(text, targetX - textWidth / 2, boxY + 28);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f));
        g2.drawLine(targetX, boxY + boxH, targetX, targetY - 25 + bob);
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(targetX, targetY - 5 + bob);
        arrowHead.addPoint(targetX - 14, targetY - 28 + bob);
        arrowHead.addPoint(targetX + 14, targetY - 28 + bob);
        g2.fillPolygon(arrowHead);
        g2.setFont(oldFont);
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

        roomManager.drawMap(g,camera);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        npcmanager.drawNPC(g, camera.getX());

        if (gsm != null) gsm.draw(g2);
        if (timeUI != null) timeUI.draw(g2);

        if (!(dialogBox == null || !dialogBox.isVisible())) {
            player.moving = false;
        }

        player.draw(g2);
        player.setVisible(isTransitioning);

        //จอมืดลงตอนไฟดับ
        if (isLightOut) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        if (gsm != null) gsm.draw(g2);
        if (timeUI != null) timeUI.draw(g2);
        RadialGradientPaint vignette = new RadialGradientPaint(
                new Point(getWidth()/2, getHeight()/2),
                Math.max(getWidth(), getHeight()) / 1.5f, // ขอบใกล้
                new float[]{0.3f, 1f},                    // มืด
                new Color[]{
                        new Color(0,0,0,0),
                        new Color(0,0,0,200)
                }
        );

        g2.setPaint(vignette);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (textBook != null && textBook.isVisible()) {
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.setFont(FontUtil.THAI);


        //task text ขวาบน
        g2.setColor(Color.WHITE);
        g2.drawString(minigameManager.taskText, 1450 , 75);

        if (shouldShowElevatorGuide()) drawElevatorGuide(g2);

        if (eventManager != null && eventManager.isShowImage()) {
            String activeEvent = eventManager.getActiveZoneName();
            if (activeEvent.equals("Elevator_Panel") && elevatorUI != null) {
                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRect(0, 0, getWidth(), getHeight());
                int uiX = (getWidth() - elevatorUI.getWidth()) / 2;
                int uiY = (getHeight() - elevatorUI.getHeight()) / 2;
                g2.drawImage(elevatorUI, uiX, uiY, null);
            }
        }

        if (dialogBox != null) dialogBox.draw(g2);

        if (showHint && hintText != null) {
            int playerScreenX = player.xDelta - camera.getX() + player.offsetX;
            int playerScreenY = player.yDelta - 20;
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(hintText);
            int h = fm.getHeight();
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(playerScreenX-10, playerScreenY - h +10, w + 20, h);
            g2.setColor(Color.WHITE);
            g2.drawString(hintText, playerScreenX , playerScreenY );
        }

        if (timeUI != null) {
            timeUI.draw(g2);
        }

        //ทรานซิชั่นถมดำ
        if (fadeAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            Graphics2D transition = (Graphics2D) g;
            transition.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            transition.setColor(Color.BLACK);
            transition.fillRect(0, 0, 1920, 1080);
            transition.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // รีเซ็ตค่ากลับ
        }

        if (showNotification && notificationText != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, notificationAlpha));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(notificationText);
            int x = (getWidth() - textWidth) / 2;
            int y = 250;
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(x - 20, y - 40, textWidth + 40, 50);
            g2.setColor(Color.YELLOW);
            g2.drawString(notificationText, x, y);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        if (showingEnding && gameEnding != null) {
            gameEnding.draw(g2);
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

    public void showNotification(String text) {
        this.notificationText = text;
        this.notificationStartTime = System.currentTimeMillis();
        this.notificationAlpha = 1f;
        this.showNotification = true;
    }

    public int getScreenHeight() { return screenHeight; }
    public int getScreenWidth() { return screenWidth; }
    public boolean getIsTransitioning() {return isTransitioning;}
    public void setDialogBox(DialogBox dialogBox) { this.dialogBox = dialogBox; }
    public Sound getSound() {return sound;}
    public boolean isInMinigame() { return minigameManager != null && minigameManager.isPlaying(); }
    public GameStateManager getGSM() { return gsm; }
    public NPCmanager getNpcmanager() { return npcmanager; }
    public MinigameManager getMinigameManager() { return minigameManager; }

    public void setLightOut(boolean lightOut) {
        isLightOut = lightOut;
    }
    public boolean getLightOut() {
        return isLightOut;
    }
    public Player getPlayer() {
        return player;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}