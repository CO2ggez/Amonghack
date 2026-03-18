package ui;

import javax.swing.*;
import java.awt.*;

public class CgLoader {
    Image cgImage;
    private boolean visible = false;

    public CgLoader(String cgName) {
        cgImage = new ImageIcon(getClass().getResource("CgImage/" + cgName + ".png")).getImage();
        if (cgImage == null){
            System.out.println("cgImage is null");
        }
    }
    public void drawCg(Graphics g){
        if(visible){
            g.drawImage(cgImage,0, 0,null);
        }

    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
    }

}
