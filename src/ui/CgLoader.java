package ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CgLoader{
    private Map<String, Image> cgGallery;
    private Image currentCg;
    private boolean visible = false;

    public CgLoader() {
        cgGallery = new HashMap<>();
        loadAllCgs();
    }

    private void loadAllCgs() {
        String[] cgNames = {"CG1-JobApplication", "CG2-JobInterview", "CG-ending-Arrest", "CG-ending-chief","CG-ending-Janitor","Cg-startday"};

        for (String name : cgNames) {
            try {
                Image img = new ImageIcon(getClass().getResource("CgImage/" + name + ".png")).getImage();
                if (img != null) {
                    cgGallery.put(name, img);
                }
            } catch (Exception e) {
                System.out.println("Could not load CG: " + name);
            }
        }
    }

    //เลือก cg ให้แสดง ใส่ชื่อไฟล์รูป
    public void setCg(String name) {
        if (cgGallery.containsKey(name)) {
            currentCg = cgGallery.get(name);
            visible = true;
        } else {
            System.out.println("CG not found: " + name);
            visible = false;
        }
    }

    public void draw(Graphics g) {
        if (visible && currentCg != null) {
            g.drawImage(currentCg, 0, 0, null);
        }
    }

    public void hide() {
        visible = false;
    }
}