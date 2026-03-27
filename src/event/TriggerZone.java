package event;

public class TriggerZone {
    private String name;
    private int x, y, width, height;

    public TriggerZone(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isHit(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width) &&
                (mouseY >= y && mouseY <= y + height);
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCenterX() {
        return x + width / 2;
    }

    public int getCenterY() {
        return y + height / 2;
    }
}