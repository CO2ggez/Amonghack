package map;

import core.InputManager;
import ui.Camera;

import java.awt.*;
import entity.Player;

public class RoomManager {
    private MapLoader mapLoader;
    private Player player;

    private String currentRoom;
    private String[] mapDataFloor2 = {"lift2","office","itsupport","server","stair2"}; //กะว่าจะให้เก็บข้อมูลการเชื่อมโยงของแต่ละห้องในนี้
    private String[] mapDataFloor1 = {"lift1","art","market","chiefoffice","stair1"};
    private String[] mapDataFloorG = {"liftG","restroom","meeting","stairG"};

    private String[] currentFloor = mapDataFloor1;
    private int currentRoomIndex = 0;

    public RoomManager(Player player) {
        this.player = player;
        mapLoader = new MapLoader(currentFloor[currentRoomIndex]);
    }

    public void changeRoomRight(Camera camera){
        if (currentRoomIndex<currentFloor.length -1){
            currentRoomIndex++;
            mapLoader = new MapLoader(currentFloor[currentRoomIndex]);

            player.xDelta = 0;

        }
    }

    public void changeRoomLeft(Camera camera){
        if (currentRoomIndex - 1 >= 0 ) {
            currentRoomIndex--;
            mapLoader = new MapLoader(currentFloor[currentRoomIndex]);

            player.xDelta = mapLoader.getWidth()-160;
            player.checkRight = false;
        }
    }

    public void drawMap(Graphics g, Camera camera) {
        mapLoader.drawMap(g, camera);
    }

    public int getWidth() {
        return mapLoader.getWidth();
    }

}
