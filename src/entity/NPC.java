package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NPC {
    protected int x;
    protected int y = 882 - 384;
    protected int width = 64*6;
    protected int height = 64*6;
    public String inRoom;

    protected BufferedImage[] idleFrames;
    protected BufferedImage[] walkFrames;

    protected int aniTick = 0;
    protected int aniIndex = 0;
    protected int aniSpeed = 13;
    protected int walkSpeed = 4;

    protected boolean visible = true;
    protected boolean isMoving = false;
    protected boolean facingLeft = true;
    protected int targetX;

    public NPC(int x,String inRoom) {
        this.x = x;
        this.inRoom = inRoom;
        this.targetX = x;
    }

    public void setIdleFrames(BufferedImage[] frames) {
        this.idleFrames = frames;
    }

    public void setWalkFrames(BufferedImage[] frames) {
        this.walkFrames = frames;
    }

    public void moveTo(int targetX) {
        this.targetX = targetX ;
        this.isMoving = true;
        this.aniIndex = 0; // เริ่มแอนิเมชันใหม่เมื่อสั่งเดิน
    }

    public void update() {

        updatePosition();
        updateAnimation();

    }

    private void updatePosition() {
        if (isMoving) {
            if (Math.abs(x - targetX) > walkSpeed) {
                if (x < targetX) {
                    x += walkSpeed;
                    facingLeft = false;
                } else {
                    x -= walkSpeed;
                    facingLeft = true;
                }
            } else {
                x = targetX;
                isMoving = false;
            }
        }
    }

    private void updateAnimation() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;

            BufferedImage[] currentFrames;
            if (isMoving) {
                currentFrames = walkFrames;
            } else {
                currentFrames = idleFrames;
            }

            if (currentFrames != null && aniIndex >= currentFrames.length) {
                aniIndex = 0;
            }
        }
    }

    public void draw(Graphics g,int cameraX) {
        if (!visible) return;

        BufferedImage[] currentFrames;
        if (isMoving) {
            currentFrames = walkFrames;
        } else {
            currentFrames = idleFrames;
        }

        if (currentFrames != null && currentFrames.length > 0) {
            // นำ x มาลบด้วย cameraX เพื่อให้ NPC อยู่กับที่ในโลกของเกม
            int screenX = x - cameraX;

            //กัน index เกินจำนวนรูป
            int indexToDraw = aniIndex;
            if (indexToDraw >= currentFrames.length) {
                indexToDraw = 0;
            }

            if (!facingLeft) {
                g.drawImage(currentFrames[indexToDraw], screenX + width, y, -width, height, null);
            } else {
                g.drawImage(currentFrames[indexToDraw], screenX, y, width, height, null);
            }
        }
    }

    public void setLocation(String inRoom, int x) {
        this.inRoom = inRoom;
        this.x = x;
    }


    public void hide() {
        visible = false;
    }

    public void show() {
        visible = true;
    }

    public int getX() {
        return x;
    }
}
