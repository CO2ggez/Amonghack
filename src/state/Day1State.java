package state;

import core.GamePanel;
import network.MinigameManager;
import ui.StoryDialog;

import java.awt.*;
import java.util.ArrayList;

public class Day1State extends AbstractState {
    public GamePanel gamePanel;
    public MinigameManager minigameManager;

    private double h;
    private double lastH;
    private boolean banIpLogTriggeredAtFive = false;
    private ArrayList<String> taskList = new ArrayList<>();

    private boolean startedEnding = true;
    private int cgIndex = 0;
    private boolean startedDialogue = false;

    // จัดการ CG คั่นก่อนจบวัน
    private boolean isPlayingEndCG = false;
    private boolean endCgTriggered = false;

    private String[] cgList = {
            "CG1-JobApplication",
            "CG2-JobInterview",
    };

    private String[][] dialogList = {
            StoryDialog.PRE_DAY0_CG1,
            StoryDialog.PRE_DAY0_CG2
    };

    public Day1State(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        minigameManager = gamePanel.getMinigameManager();

        setupNPC();
        setSpecialTask();

        taskList.add("lan");
        taskList.add("terminal");
        taskList.add("lan");
        taskList.add("terminal");

        minigameManager.resetTask();
    }

    @Override
    public void update() {

        //CG day 0
        if (startedEnding) {
            startedEnding = false;

            gamePanel.timeManager.setPaused(true);
            gamePanel.showingEnding = true;

            cgIndex = 0;

            gamePanel.gameEnding.startEnding(cgList[cgIndex], dialogList[cgIndex]);
            return;
        }

        // เปลี่ยนรูป CG และออกจากโหมด CG
        if (gamePanel.showingEnding) {

            if (gamePanel.gameEnding.isFinished()) {

                if (isPlayingEndCG) {
                    // บังคับจบวัน CG ห้องนอน
                    gamePanel.showingEnding = false;
                    isPlayingEndCG = false;
                    gamePanel.timeManager.forceEndDay(); // บังคับจบวันเลย
                } else {
                    // CG เปิดเกม
                    cgIndex++;

                    if (cgIndex < cgList.length) {
                        gamePanel.gameEnding.startEnding(cgList[cgIndex], dialogList[cgIndex]);
                    } else {
                        // เริ่มเดินเวลา ตอน CG เปิดเกมจบ
                        gamePanel.showingEnding = false;
                        gamePanel.timeManager.setPaused(false);

                        // เปิดเพลง เดะมาแก้อีกที
                        if (gamePanel.getSound() != null) {
                            gamePanel.getSound().loopSound("bg1");
                        }
                    }
                }
            }
            return;
        }

        // เริ่ม Day1
        if (!startedDialogue) {
            startedDialogue = true;
            gamePanel.dialogBox.startDialog(StoryDialog.DAY1_LIFT1);
        }

        // ดึงเวลามา
        h = this.gamePanel.timeManager.getHours();

        // เควส / มินิเกม
        if (!banIpLogTriggeredAtFive && h >= 5.0) {
            minigameManager.setTask("baniplog");
            banIpLogTriggeredAtFive = true;
            lastH = h;
            return;
        }

        if (h != lastH) {
            if ((h + 1) % 1.5 == 0 && !taskList.isEmpty()) {
                minigameManager.setTask(taskList.get(0));
                taskList.remove(0);
                lastH = h;
            }
        }

        // เช็คที่ 5.9 (ประมาณ 05:54 AM) ก่อนจะตัดจบวันตอน 06:00 AM
        if (h >= 5.9 && !endCgTriggered) {
            endCgTriggered = true;
            isPlayingEndCG = true;

            gamePanel.timeManager.setPaused(true); // หยุดเวลาก่อน
            gamePanel.showingEnding = true;        // เข้าโหมดโชว์ CG
            gamePanel.gameEnding.startEnding("CG3-BedRoom", StoryDialog.DAY1_BEDROOM); // โชว์ CG ห้องนอน
            return;
        }
    }

    @Override
    public void draw(Graphics2D g) {}

    public void setupNPC(){
        gamePanel.getNpcmanager().showAllNPCs();

        gamePanel.getNpcmanager().janitor.setLocation("art", 1600);
        gamePanel.getNpcmanager().hr.setLocation("office", 1100);
        gamePanel.getNpcmanager().boss.setLocation("chiefoffice", 1600);
        gamePanel.getNpcmanager().itsupport.setLocation("itsupport", 1400);
    }

    public void setSpecialTask(){
        gamePanel.getMinigameManager().taskBoss = true;
        gamePanel.getMinigameManager().taskJanitor = true;
    }
}