package ui;
import core.GamePanel;
import map.MapLoader;
import entity.Player;

public class Camera {
    private int x;

    private final int screenWidth,screenHeight;
    private int worldWidth;
    private final MapLoader mapLoader;

    public Camera(GamePanel panel,MapLoader mapLoader) {
        x = 0;

        screenWidth = panel.getScreenWidth();
        screenHeight = panel.getScreenHeight();
        this.mapLoader = mapLoader;

    }

    public void update(Player player) {
        int targetX = player.getxDelta()  - (screenWidth/2 - 132/2);

        worldWidth = mapLoader.getWidth();

        x = Math.max(0, Math.min(targetX, worldWidth - screenWidth));

    }

    public int getX() { return x; }
    public int getScreenHeight() { return screenHeight; }

    public int getWorldWidth() {
        return worldWidth;
    }
}
