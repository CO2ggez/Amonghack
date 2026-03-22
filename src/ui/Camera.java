package ui;
import core.GamePanel;
import entity.Player;
import map.RoomManager;

public class Camera {
    private int x;

    private final int screenWidth,screenHeight;
    private int worldWidth;
    private final RoomManager roomManager;

    public Camera(GamePanel panel,RoomManager roomManager) {
        x = 0;

        screenWidth = panel.getScreenWidth();
        screenHeight = panel.getScreenHeight();
        this.roomManager = roomManager;

    }

    public void update(Player player) {

        int targetX = player.getxDelta()  - (screenWidth/2 - 132/2);

        worldWidth = roomManager.getWidth();

        x = Math.max(0, Math.min(targetX, worldWidth - screenWidth));//ให้กล้องไม่เลยขอบแมพ

    }

    public int getX() { return x; }//ส่งให้แมพขยับตามกล้องอีกที
    public int getScreenHeight() { return screenHeight; }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getWorldWidth() {
        return worldWidth;
    }
}
