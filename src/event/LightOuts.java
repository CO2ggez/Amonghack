package event;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import network.Minigame;
import network.MinigameLocation;
import network.MinigameManager;
import javax.swing.*;
import javax.swing.Timer;

public class LightOuts implements Minigame {
    public JPanel breaker;
    public MinigameManager l_manager;
    public Image breaker_img_on;
    public Image breaker_img_off;
    public TriggerZone switches;
    private boolean isOn; // State: true = on, false = off
    private Point panelLocation; // Store panel location for hit detection

    private boolean waiting = false; //เอาไว้กันกด ตอนรอปิดมินิเกม

    @Override
    public JPanel getPanel() {
        return this.breaker;
    }
    public LightOuts(MinigameManager l_manager) {
        setManager(l_manager);
        initialize();
    }

    private void initialize() {
        // Load images
        try {
            breaker_img_on = new ImageIcon(getClass().getResource("images/breaker_on.png")).getImage();
            breaker_img_off = new ImageIcon(getClass().getResource("images/breaker_off.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        // Set initial state (default: off)
        isOn = false;

        // Create the panel with null layout
        breaker = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image currentImage = isOn ? breaker_img_on : breaker_img_off;
                if (currentImage != null) {
                    g.drawImage(currentImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback color if images not loaded
                    g.setColor(isOn ? Color.YELLOW : Color.DARK_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        breaker.setBounds(0, 0, 1920, 1080);
        breaker.setLayout(null);
        breaker.setPreferredSize(new Dimension(1920, 1080));

        // Create trigger zone for the switch area
        // You can adjust these coordinates based on where the switch is in your image
        switches = new TriggerZone("light_switch", 780, 300, 325, 375);

        // Add mouse listener to the panel
        breaker.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (waiting) return;
                // Get the absolute coordinates of the click relative to the panel
                int clickX = e.getX();
                int clickY = e.getY();

                // Check if click is within the trigger zone
                if (switches.isHit(clickX, clickY)) {
                    toggleState();
                }
            }
        });
    }

    public void toggleState() {
        isOn = !isOn;
        breaker.repaint();

        // สับสวิตช์แล้วรอสามวิค่อยปิด
        if (isOn) {
            waiting = true;
            Timer timer = new Timer(1000, e -> {
                l_manager.onWin();
                waiting = false;
            });
            timer.setRepeats(false); // ให้ทำครั้งเดียว
            timer.start();
        }
    }



    public boolean isOn() {
        return isOn;
    }

    public void setState(boolean on) {
        if (this.isOn != on) {
            isOn = on;
            breaker.repaint();
        }
    }



    public void setManager(MinigameManager manager) {
        this.l_manager = manager;
    }

}