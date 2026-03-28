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

        int drawX;
        if (width < camera.getScreenWidth()) {
            drawX = (camera.getScreenWidth() - width) / 2; //
        } else {
            drawX = -camera.getX(); //ให้แมพเลื่อนซ้าย
        }

        g.drawImage(mapImage, drawX, (camera.getScreenHeight()-height) / 2, null);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
