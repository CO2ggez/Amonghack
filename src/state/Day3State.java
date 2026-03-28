package state;

import core.GamePanel;
import network.MinigameManager;
import ui.StoryDialog;


import java.awt.*;
import java.util.ArrayList;

public class Day3State extends AbstractState {
    public GamePanel gamePanel;
    public MinigameManager minigameManager;

    private double h;
    private double lastH; //เพื่อเช็คว่าคำสั่งนั้นถูกเรียกใช้ครั้งที่แล้วเมื่อใด
    private ArrayList<String> taskList = new ArrayList<>();
    private boolean banIpLogTriggeredAtFive = false;

    private String obj = "รายงานความผิดปกติกับหัวหน้า";
    private boolean startedDialogue = false;
    private boolean startedLightout = false;

    private boolean startedDialogue2 = false;

    // จัดการ CG คั่นก่อนจบวัน
    private boolean isPlayingEndCG = false;
    private boolean endCgTriggered = false;

    public Day3State(GamePanel gamePanel) {
        //ไว้จัดฉาก เตรียมสิ่งต่าง ๆ เช่นโหลดภาพ CG เริ่มวัน หรือเอา NPC มาวางรอไว้
        this.gamePanel = gamePanel;
        minigameManager = gamePanel.getMinigameManager();

        gamePanel.getSound().setVolume("bg1", 0.5f);
        gamePanel.getSound().loopSound("bg1");

        // วาร์ปกลับไป lift ชั้น 1
        gamePanel.getRoomManager().setRoom(gamePanel.getRoomManager().mapDataFloor1, 0);
        // เซตตำแหน่งตัวละครให้กลับมาที่ลิฟต์
        gamePanel.getPlayer().xDelta = 100;
        gamePanel.getPlayer().checkRight = true;

        setupNPC();
        setSpecialTask();

        //task ในวันนี้
        taskList.add("terminal");
        taskList.add("lan");
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
                    gamePanel.getSound().stopSound("bg_dayEnd");
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
                if (!minigameManager.taskLightOut) {
                    minigameManager.setTask(taskList.getFirst());
                    taskList.remove(taskList.getFirst());

                    lastH = h;
                    System.out.println(taskList.size());
                }

            }
        }

        // ดักเวลาก่อน 6 โมงเช้า
        if (h >= 5.9 && !endCgTriggered) {
            endCgTriggered = true;
            isPlayingEndCG = true;

            gamePanel.getSound().stopSound("bg1");
            gamePanel.getSound().setVolume("bg_dayEnd", 0.5f);
            gamePanel.getSound().loopSound("bg_dayEnd");

            gamePanel.timeManager.setPaused(true); // หยุดเวลาก่อน
            gamePanel.showingEnding = true;        // เข้าโหมดโชว์ CG

            // เรียกใช้รูปห้องนอน
            gamePanel.gameEnding.startEnding("CG3-BedRoom", StoryDialog.DAY3_BEDROOM);
            return;
        }

        if (!obj.equals("") && minigameManager.taskText.equals("")) {
            minigameManager.taskText = obj;
        } else if (gamePanel.finishedObjective) {
            if (minigameManager.taskText.equals(obj)) {
                minigameManager.taskText = "";
            }
            obj = "";
        }

        if (minigameManager.taskLightOut) {
            startedLightout = true;
        }

        //dialogue หลังไฟดับ
        if (startedLightout && !startedDialogue && !gamePanel.getLightOut()) {
            startedDialogue = true;
            gamePanel.getNpcmanager().janitor.setLocation("liftG", 1920);
            gamePanel.getNpcmanager().janitor.moveTo(500);
            gamePanel.getPlayer().checkRight = true;
            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY3_LIFTG);
            gamePanel.getInputManager().progressDay3 = 3;
        }

        //dialogue หลังเช็คlog
        if (minigameManager.afterMinigameDialogue && !startedDialogue2 && gamePanel.getInputManager().progressDay3 == 3) {
            startedDialogue2 = true;

            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY3_SERVER);
            gamePanel.getInputManager().progressDay3 = 4;
            try {
                gamePanel.textBook.update();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void draw(Graphics2D g) {
        //เอาไว้ วาดภาพหรือใส่ UI พิเศษ ที่มีเฉพาะวันนั้น
    }

    public void setupNPC() {
        gamePanel.getNpcmanager().showAllNPCs();

        gamePanel.getNpcmanager().janitor.setLocation(null, 1600);
        gamePanel.getNpcmanager().hr.setLocation(null, 1100);
        gamePanel.getNpcmanager().boss.setLocation("lift1", 230);
        gamePanel.getNpcmanager().itsupport.setLocation("itsupport", 350);
    }

    public void setSpecialTask() {
        gamePanel.getMinigameManager().taskBoss = true;
        gamePanel.getMinigameManager().taskJanitor = true;
    }
}
