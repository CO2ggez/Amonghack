package map;

import java.awt.*;
import javax.swing.*;
import ui.Camera;

public class MapLoader {
    private Image mapImage;
    private int width;
    private int height;

    public MapLoader(String roomName) {
        mapImage = new  ImageIcon(getClass().getResource("mapImage/" + roomName + ".png")).getImage();
        if (mapImage == null){
            System.out.println("mapImage is null");
        }else{
            width = mapImage.getWidth(null);
            height = mapImage.getHeight(null);
        }

    }

    public void drawMap(Graphics g,Camera camera) {
        g.drawImage(mapImage,-camera.getX(), (camera.getScreenHeight()-height)/2, null);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
