package map;

import core.InputManager;
import ui.Camera;

import java.awt.*;
import entity.Player;

public class RoomManager {
    private MapLoader mapLoader;
    private Player player;

    public String currentRoom;
    public String[] mapDataFloor2 = {"lift2","office","itsupport","server","stair2"}; //กะว่าจะให้เก็บข้อมูลการเชื่อมโยงของแต่ละห้องในนี้
    public String[] mapDataFloor1 = {"lift1","art","market","chiefoffice","stair1"};
    public String[] mapDataFloorG = {"liftG","restroom","meeting","stairG"};

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

    // เมธอดสำหรับเปลี่ยนชั้น
    public void changeFloor(String[] targetFloor, String targetRoomName, int newX) {
        this.currentFloor = targetFloor;
        // หาว่าห้องบันไดที่ไปถึง อยู่ลำดับที่เท่าไหร่ในชั้นใหม่
        for (int i = 0; i < currentFloor.length; i++) {
            if (currentFloor[i].equals(targetRoomName)) {
                currentRoomIndex = i;
                break;
            }
        }
        // โหลดแมพใหม่
        mapLoader = new MapLoader(currentFloor[currentRoomIndex]);
        // วางตัวละครไว้ที่จุดบันไดของชั้นใหม่
        player.xDelta = newX;
    }

    // เพิ่ม Getter เพื่อให้ InputManager เช็คชื่อห้องปัจจุบันได้
    public String getCurrentRoomName() {
        return currentFloor[currentRoomIndex];
    }

    public void drawMap(Graphics g, Camera camera) {
        mapLoader.drawMap(g, camera);
    }

    public int getWidth() {
        return mapLoader.getWidth();
    }

}
