package network;

import javax.swing.*;
import core.GamePanel;

public class MinigameManager { //เอาไว้นับว่ามีกี่เกมที่ทำสำเร็จ และ ศูนย์รวมเริ่มมินิเกม
    private GamePanel panel;
    private JPanel currentGamePanel;
    public boolean taskLan = false;

    private int score = 0;

    public MinigameManager(GamePanel panel) {
        this.panel = panel;
    }

    //ต้องไปสุ่ม task มาก่อนค่อยเรียกใช้อันนี้
    public void setTask(String type) {

        switch (type) {
            case "lan" -> taskLan = true; //ให้ taskLan เป็น true เพื่อให้้ input manager เช็คให้กด f ที่จุดที่เล่นมินิเกมนั้นได้
        }

    }

    public void startTask(){//ใน inputManager เมื่อplayer กด F ที่ตำแหน่งเล่นเกม จะเรียกใชเอันนี้ สร้างobj minigame
        Minigame game = null;

        if (taskLan) {
            game = new LanCable(this);
            taskLan = false;
        }

        if (game == null) return;

        currentGamePanel = game.getPanel();

        panel.add(currentGamePanel);
        panel.repaint();
        currentGamePanel.requestFocusInWindow();
    }

    public void closeGame() {
        if (currentGamePanel != null) {
            panel.remove(currentGamePanel);
            panel.repaint();
        }
    }

    public void onWin() {
        score++;
        closeGame();
    }

    public int getScore() {
        return score;
    }

}
