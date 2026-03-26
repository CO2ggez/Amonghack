package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NPC {
    protected int x;
    protected final int y = 882 - 384;
    protected final int width = 64*6;
    protected final int height = 64*6;
    protected String inRoom;

    protected BufferedImage[] idleFrames;
    protected BufferedImage[] walkFrames;

    protected int aniTick = 0;
    protected int aniIndex = 0;
    protected int aniSpeed = 13;
    protected int walkSpeed = 5;

    protected boolean visible = true;
    protected boolean isMoving = false;

    protected int targetX;

    public NPC(int x,String inRoom) {
        this.x = x;
        this.inRoom = inRoom;
        this.targetX = x;
    }

    public void setIdleFrames(BufferedImage[] frames) {
        this.idleFrames = frames;
    }

    public void update() {
        aniTick++;

        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;

            if (aniIndex >= idleFrames.length) {
                aniIndex = 0;
            }
        }
    }

    public void draw(Graphics g) {
        if (!visible) return;

        g.drawImage(idleFrames[aniIndex], x, y, width, height, null);
    }

    public void hide() {
        visible = false;
    }

    public void show() {
        visible = true;
    }
}
