package event;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import network.Minigame;
import network.MinigameManager;
import javax.swing.*;

public class LightOuts implements Minigame {
    public JPanel breaker;
    public MinigameManager l_manager;
    public Image breaker_img_on;
    public Image breaker_img_off;
    public TriggerZone switches;
    private boolean isOn; // State: true = on, false = off
    private Point panelLocation; // Store panel location for hit detection

    public LightOuts() {
        initialize();
    }

    private void initialize() {
        // Load images
        try {
            breaker_img_on = new ImageIcon("images/breaker_on.png").getImage();
            breaker_img_off = new ImageIcon("images/breaker_off.png").getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        // Set initial state (default: off)
        isOn = false;

        // Create the panel with null layout.
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

        breaker.setLayout(null);
        breaker.setPreferredSize(new Dimension(400, 500));

        // Create trigger zone for the switch area
        switches = new TriggerZone("light_switch", 150, 200, 100, 100);

        // Add mouse listener to the panel
        breaker.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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

    @Override
    public JPanel getPanel() {
        return this.breaker;
    }

    public void setManager(MinigameManager manager) {
        this.l_manager = manager;
    }

}