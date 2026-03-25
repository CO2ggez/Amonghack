package network;

import javax.swing.*;
import core.GamePanel;

import java.util.*;


public class MinigameManager { //เอาไว้นับว่ามีกี่เกมที่ทำสำเร็จ และ ศูนย์รวมเริ่มมินิเกม
    private GamePanel panel;
    private JPanel currentGamePanel;

    public boolean taskLan = false;
    public String[] currentLanLocation = {"","",""};

    private boolean isPlaying = false;
    private int score = 0;
    public String taskText = "";

    //hashmap เก็บข้อมูลว่า lan มีที่ตำแหน่งไหนห้องไหน เพื่อเอาไปสุ่ม
    Map<String, int[]> zoneMap = new HashMap<>();

    public MinigameManager(GamePanel panel) {
        this.panel = panel;
    }

    //ต้องไปสุ่ม task มาก่อนค่อยเรียกใช้อันนี้
    public void setTask(String type) {

        switch (type) {
            case "lan":
                //ให้ taskLan เป็น true เพื่อให้้ input manager เช็คให้กด f ที่จุดที่เล่นมินิเกมนั้นได้
                taskLan = true;
                currentLanLocation = randomLanRoom();

                taskText = "เชื่อมสายแลนที่ห้อง" + currentLanLocation[0];
                System.out.println(taskText);

                break;

        }

    }

    public void startTask(){//ใน inputManager เมื่อplayer กด F ที่ตำแหน่งเล่นเกม จะเรียกใชเอันนี้ สร้างobj minigame
        isPlaying = true;

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
        currentGamePanel.setBounds(0, 0, 1920, 1080);
        currentGamePanel.setFocusable(true);
        currentGamePanel.setOpaque(false);
    }

    public void closeGame() {
        if (currentGamePanel != null) {
            panel.remove(currentGamePanel);
            panel.repaint();
        }
        isPlaying = false;
        panel.requestFocusInWindow();
        taskText = "";

    }

    public void onWin() {
        score++;
        closeGame();

        System.out.println("คะแนนมินิเกมตอนนี้ " + score);
    }

    public int getScore() {
        return score;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String[] randomLanRoom(){
        List<Map.Entry<String, int[]>> list = new ArrayList<>(MinigameLocation.lanLocation.entrySet());

        Map.Entry<String, int[]> random = list.get(new Random().nextInt(list.size()));

        return new String[]{
                random.getKey(),
                String.valueOf(random.getValue()[0]),
                String.valueOf(random.getValue()[1])
        };
    }
}
