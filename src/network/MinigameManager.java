package network;

import javax.swing.*;
import core.GamePanel;
import event.LightOuts;

import java.util.*;


public class MinigameManager { //เอาไว้นับว่ามีกี่เกมที่ทำสำเร็จ และ ศูนย์รวมเริ่มมินิเกม
    private GamePanel panel;
    private JPanel currentGamePanel;

    public boolean taskLan = false;
    public String[] currentLanLocation = {"","",""};

    public boolean taskTerminal = false;
    public String[] currentTerminalLocation = {"","",""};

    public boolean taskBanIpLog = false;
    public String[] currentBanIpLogLocation = {"","",""};

    public boolean taskLightOut = false;
    public String[] currentLightOutLocation = {"liftG","120","270"};

    private boolean isPlaying = false;
    public int score = 0; //check gameover (8 point)
    public String taskText = "";

    public int helpJanitorScore = 0; //คะแนน สำหรับฉากจบภารโรง (check 4 point)
    public int helpBossScore = 0;    //คะแนน สำหรับฉากจบหัวหน้า (check 4 point)
    public boolean taskBoss = false;
    public boolean taskJanitor = false;
    private boolean currentTaskCompleted = false;

    public HashMap<String, Boolean> allMinigame = new HashMap<String, Boolean>();

    public MinigameManager(GamePanel panel) {

        this.panel = panel;

        allMinigame.put("lan", false);
        allMinigame.put("terminal", false);
        allMinigame.put("baniplog", false);
        allMinigame.put("lightOut", false);
    }



    //เอาไว้ให้คลาสอื่นเรียก sound
    public GamePanel getGamePanel(){
        return panel;
    }

    //ต้องไปสุ่ม task มาก่อนค่อยเรียกใช้อันนี้
    public void setTask(String type) {

        //ปิดทุกtask
        allMinigame.keySet().forEach(key -> updateTaskStatus(key, false));
        //เปิดtaskนี้
        updateTaskStatus(type, true);

        if (type.equals("lan")) {
            currentLanLocation = randomLanRoom();
            taskText = "เชื่อมสายแลนที่ห้อง " + currentLanLocation[0];
        } else if (type.equals("terminal")) {
            currentTerminalLocation = randomTerminalRoom();
            taskText = "เปิดใช้ Terminal ที่ห้อง " + currentTerminalLocation[0];
        } else if (type.equals("baniplog")) {
            currentBanIpLogLocation = fixedBanIpLogLocation();
            taskText = "ตรวจสอบ Server Log ที่ห้อง " + currentBanIpLogLocation[0];
        } else if (type.equals("lightOut")) {
            panel.setLightOut(true);
            taskText = "ไปเช็คตู้ไฟที่ลิฟต์ชั้น G";
        }


    }

    public void startTask(){//ใน inputManager เมื่อplayer กด F ที่ตำแหน่งเล่นเกม จะเรียกใชเอันนี้ สร้างobj minigame
        if (isPlaying) return; //กันเปิดมินิเกมซ้อน

        isPlaying = true;

        currentTaskCompleted = false;

        Minigame game = null;

        if (taskLan) {
            game = new LanCable(this);
            updateTaskStatus("lan", false);
            taskLan = false;
        } else if (taskTerminal) {
            game = new TerminalMinigame(this);
            updateTaskStatus("terminal", false);
            taskTerminal = false;
        } else if (taskBanIpLog) {
            int currentDay = 1;
            if (panel.getGSM() != null) {
                currentDay = panel.getGSM().getCurrentDay();
            }

            game = new BanIpLogMinigame(this, currentDay);
            updateTaskStatus("baniplog", false);
            taskBanIpLog = false;
        } else if (taskLightOut) {
            game = new LightOuts(this);
            updateTaskStatus("lightOut", false);
            taskLightOut = false;
        }

        if (game == null) {
            isPlaying = false; //ถ้าไม่มีมินิเกมให้เปิด ก็คืนสถานะ
            return;
        }

        currentGamePanel = game.getPanel();

        currentGamePanel.setBounds(0, 0, 1920, 1080);
        currentGamePanel.setFocusable(true);
        currentGamePanel.setOpaque(false);

        panel.add(currentGamePanel);
        panel.revalidate();
        panel.repaint();
        currentGamePanel.requestFocusInWindow();

    }

    public void closeGame() {
        if (currentGamePanel != null) {
            panel.remove(currentGamePanel);
            panel.repaint();
        }

        isPlaying = false;
        currentTaskCompleted = false;
        panel.requestFocusInWindow();

        if(!allMinigame.containsValue(true)){

            taskText = "";
        }
    }

    public void onWinStayOpen() {
        if (currentTaskCompleted) return;

        currentTaskCompleted = true;
        score++;

        System.out.println("คะแนนมินิเกมตอนนี้ " + score);
    }

    public boolean isCurrentTaskCompleted() {
        return currentTaskCompleted;
    }

    public void onWin() {
        score++;
        closeGame();
        panel.setLightOut(false);

        System.out.println("คะแนนมินิเกมตอนนี้ " + score);
    }

    //มีไว้ไมไม่รุ้ เผื่อไว้
    public void resetTask(){
        allMinigame.keySet().forEach(key -> updateTaskStatus(key, false));
        taskText = "";
        isPlaying = false;

    }

    public int getScore() {
        return score;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    //update สถานะทุกtask เพื่อกันtask ทับกัน หรือเล่นtask อยุ่แล้วอีกtask มา
    private void updateTaskStatus(String type, boolean isActive) {
        if (type.equals("lan")) {
            taskLan = isActive;
            allMinigame.put("lan", isActive);
        } else if (type.equals("terminal")) {
            taskTerminal = isActive;
            allMinigame.put("terminal", isActive);
        } else if (type.equals("baniplog")) {
            taskBanIpLog = isActive;
            allMinigame.put("baniplog", isActive);
        }else  if (type.equals("lightOut")) {
            taskLightOut = isActive;
            allMinigame.put("lightOut", isActive);
        }

        //เพิ่มมินิเกมอื่น
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
    // ฟังก์ชันสุ่มห้องสำหรับ Terminal
    public String[] randomTerminalRoom(){
        List<Map.Entry<String, int[]>> list = new ArrayList<>(MinigameLocation.terminalLocation.entrySet());
        Map.Entry<String, int[]> random = list.get(new Random().nextInt(list.size()));

        return new String[]{
                random.getKey(),
                String.valueOf(random.getValue()[0]),
                String.valueOf(random.getValue()[1])
        };
    }

    public String[] fixedBanIpLogLocation(){
        int[] location = MinigameLocation.getBanIpLog("server");

        return new String[]{
                "server",
                String.valueOf(location[0]),
                String.valueOf(location[1])
        };
    }
}
