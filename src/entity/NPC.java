package entity;

public class NPC {
    protected int x;
    protected int y;
    protected String[] dialogues;

    public NPC(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String[] getDialogues() {
        return dialogues;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}