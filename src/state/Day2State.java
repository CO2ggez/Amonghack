package state;

import core.GamePanel;
import network.MinigameManager;
import ui.StoryDialog;
import java.awt.*;
import java.util.ArrayList;

public class Day2State extends AbstractState {
    public GamePanel gamePanel;
    public MinigameManager minigameManager;

    private double h;
    private double lastH; //เพื่อเช็คว่าคำสั่งนั้นถูกเรียกใช้ครั้งที่แล้วเมื่อใด
    private ArrayList<String> taskList = new ArrayList<>();
    private boolean banIpLogTriggeredAtFive = false;

    // จัดการ CG คั่นก่อนจบวัน
    private boolean isPlayingEndCG = false;
    private boolean endCgTriggered = false;

    public Day2State(GamePanel gamePanel) {
        //ไว้จัดฉาก เตรียมสิ่งต่าง ๆ เช่นโหลดภาพ CG เริ่มวัน หรือเอา NPC มาวางรอไว้
        this.gamePanel = gamePanel;
        minigameManager = gamePanel.getMinigameManager();

        setupNPC();
        setSpecialTask();

        gamePanel.getPlayer().xDelta = 100;
        gamePanel.getPlayer().checkRight = true;

        //task ในวันนี้
        taskList.add("lan");
        taskList.add("terminal");
        taskList.add("lan");
        taskList.add("terminal");

        minigameManager.resetTask();

        // มั่นใจว่าเริ่มวันมาต้องไม่ค้างหน้าจอ CG
        gamePanel.showingEnding = false;
        gamePanel.timeManager.setPaused(false);
    }

    @Override
    public void update() {
        // บังคับจบวัน CG ห้องนอน
        if (gamePanel.showingEnding) {
            if (gamePanel.gameEnding.isFinished()) {
                if (isPlayingEndCG) {
                    gamePanel.showingEnding = false;
                    isPlayingEndCG = false;
                    gamePanel.timeManager.forceEndDay();
                } else {
                    gamePanel.showingEnding = false;
                    gamePanel.timeManager.setPaused(false);
                }
            }
            return;
        }

        //เขียนเงื่อนไขดักเหตุการณ์ประจำวัน เช่น "ถ้าเวลาในเกมเดินถึงตี 2 ให้ทริกเกอร์ไฟดับ"
        h = this.gamePanel.timeManager.getHours();

        if (!banIpLogTriggeredAtFive && h >= 5.0) {
            minigameManager.setTask("baniplog");
            banIpLogTriggeredAtFive = true;
            lastH = h;
            return;
        }

        if (h != lastH) {
            if ((h + 1) % 1.5 == 0 && !taskList.isEmpty()) {
                //settask ทีละเกมแล้วลบออก
                minigameManager.setTask(taskList.get(0));
                taskList.remove(0);

                lastH = h;
                System.out.println(taskList.size());
            }
        }

        // ดักเวลาก่อน 6 โมงเช้า
        if (h >= 5.9 && !endCgTriggered) {
            endCgTriggered = true;
            isPlayingEndCG = true;

            gamePanel.timeManager.setPaused(true); // หยุดเวลาก่อน
            gamePanel.showingEnding = true;        // เข้าโหมดโชว์ CG

            // เรียกใช้รูปห้องนอน
            gamePanel.gameEnding.startEnding("CG3-BedRoom", StoryDialog.DAY2_BEDROOM);
            return;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        //เอาไว้ วาดภาพหรือใส่ UI พิเศษ ที่มีเฉพาะวันนั้น
    }

    public void setupNPC() {
        gamePanel.getNpcmanager().showAllNPCs();

        gamePanel.getNpcmanager().janitor.setLocation("meeting", 2100);
        gamePanel.getNpcmanager().hr.setLocation("office", 1100);
        gamePanel.getNpcmanager().boss.setLocation(null, 1000);
        gamePanel.getNpcmanager().itsupport.setLocation("itsupport", 2500);
    }

    public void setSpecialTask() {
        gamePanel.getMinigameManager().taskBoss = true;
        gamePanel.getMinigameManager().taskJanitor = true;
    }
}