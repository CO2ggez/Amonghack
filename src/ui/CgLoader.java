package ui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import util.FontUtil;

public class CgLoader{
    private Map<String, Image> cgGallery;
    private Image currentCg;
    private boolean visible = false;
    private String currentText = "";
    private Font storyFont = FontUtil.THAI.deriveFont(Font.PLAIN, 50f);
    private Image dialogBoxImg;
    private Image dialogBoxImg2;

    public CgLoader() {
        cgGallery = new HashMap<>();
        loadAllCgs();
        loadDialogBox();
    }

    private void loadDialogBox() {
        try {
            dialogBoxImg = new ImageIcon(getClass().getResource("/ui/DialogBox.png")).getImage();
            dialogBoxImg2 = new ImageIcon(getClass().getResource("/ui/DialogBox2.png")).getImage();
        } catch (Exception e) {
            System.out.println("Could not load DialogBox.png");
        }
    }

    private void loadAllCgs() {
        String[] cgNames = {"CG1-JobApplication", "CG2-JobInterview","CG3-BedRoom", "CG-ending-Arrest", "CG-ending-chief","CG-ending-Janitor", "CG-gameover"};
        for (String name : cgNames) {
            try {
                Image img = new ImageIcon(getClass().getResource("/ui/CgImage/" + name + ".png")).getImage();
                if (img != null) {
                    cgGallery.put(name, img);
                }
            } catch (Exception e) {
                System.out.println("Could not load CG: " + name);
            }
        }
    }

    public void setCg(String name) {
        if (cgGallery.containsKey(name)) {
            currentCg = cgGallery.get(name);
            visible = true;
            this.currentText = ""; 
        } else {
            System.out.println("CG not found: " + name);
            visible = false;
        }
    }

    public void setCgWithStory(String name, String storyText) {
        setCg(name); 
        if (visible) {
            this.currentText = storyText; 
        }
    }

    public void draw(Graphics g) {
        if (visible && currentCg != null) {

            g.drawImage(currentCg, 0, 0, 1920, 1080, null);

            if (currentText != null && !currentText.isEmpty()) {

                boolean hasName = currentText.contains(":");
                Image boxToDraw = hasName ? dialogBoxImg : dialogBoxImg2;

                if (boxToDraw != null) {

                    g.drawImage(boxToDraw, 0, 0, null);

                    g.setColor(Color.WHITE);
                    g.setFont(storyFont);

                    if (hasName) {
                        String[] parts = currentText.split(":", 2);

                        String name = parts[0].trim();
                        String text = parts[1].trim();

                        g.drawString(name, 70*6 +30, 870 -70);
                        g.drawString(text,70*6, 870);

                    }else{
                        g.drawString(currentText, 70*6, 870);
                    }


                } else {
                    int boxX = 250;
                    int boxY = 750;
                    int boxWidth = 1420;
                    int boxHeight = 250;

                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(boxX, boxY, boxWidth, boxHeight);

                    g.setColor(Color.WHITE);
                    g.setFont(storyFont);
                    g.drawString(currentText, boxX + 60, boxY + 60);
                }
            }
        }
    }

    public void hide() {
        visible = false;
        currentText = ""; 
    }
}