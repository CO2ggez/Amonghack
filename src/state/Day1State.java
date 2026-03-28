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

    private boolean janitorWalked = false;
    private boolean hrWalked = false;
    private boolean startedDialogue2 =false;

    private String[] cgList = {
            "CG1-JobApplication",
            "CG2-JobInterview",
    };

    private String[][] dialogList = {
            StoryDialog.PRE_DAY0_CG1,
            StoryDialog.PRE_DAY0_CG2
    };

    private ArrayList<String> objList = new ArrayList<>();

    public Day1State(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        minigameManager = gamePanel.getMinigameManager();

        setupNPC();
        setSpecialTask();

        taskList.add("lan");
        taskList.add("terminal");
        taskList.add("terminal");
        taskList.add("terminal");
        taskList.add("lan");

        objList.add("ไปพบหัวหน้าที่ห้อง Chief Office");
        objList.add("ไปพบเพื่อนร่วมงานที่ห้อง IT Support");
        objList.add("ไปห้อง Server Room");
        objList.add("ไปห้อง Art Department");

        minigameManager.resetTask();
    }

    @Override
    public void update() {

        //เล่น cgแรก
        if (startedEnding) {
            gamePanel.getSound().loopSound("bg_cg_day0");
            startedEnding = false;

            gamePanel.timeManager.setPaused(true);
            gamePanel.showingEnding = true;

            cgIndex = 0;

            gamePanel.gameEnding.startEnding(cgList[cgIndex], dialogList[cgIndex]);
            return;
        }

        //เล่น cg ต่อตอนเสร็จ cg นั้นๆ
        if (gamePanel.showingEnding) {
            if (gamePanel.gameEnding.isFinished()) {

                // เช็คว่าเป็น CG จบวัน
                if (isPlayingEndCG) {
                    gamePanel.getSound().stopSound("bg_dayEnd");
                    gamePanel.showingEnding = false;
                    isPlayingEndCG = false;
                    gamePanel.timeManager.forceEndDay();
                    return;
                }

                cgIndex++;

                if (cgIndex < cgList.length) {
                    gamePanel.gameEnding.startEnding(cgList[cgIndex], dialogList[cgIndex]);
                } else {
                    //เริ่มเดินเวลา ตอนcg หมด
                    gamePanel.getSound().stopSound("bg_cg_day0");
                    gamePanel.getSound().loopSound("bg1");
                    gamePanel.showingEnding = false;
                    gamePanel.timeManager.setPaused(false);
                }
            }

            return;
        }

        //dialogue ตอนเข้าแมพ
        if (!startedDialogue) {
            startedDialogue = true;
            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY1_LIFT1);
        }

        //ตอนคุยกับ janitor ที่ห้อง art
        if(gamePanel.getInputManager().talkedToJanitor == true && janitorWalked == false) {
            if (gamePanel.dialogBox == null || !gamePanel.dialogBox.isVisible()){
                janitorWalked = true;
                gamePanel.getNpcmanager().janitor.moveTo(3200);
            }

        }

        //ตอนคุยกับ hr ก่อน hr เดินออกฉากไป
        if(gamePanel.getInputManager().talkedToHR == true && hrWalked == false) {
            if (gamePanel.dialogBox == null || !gamePanel.dialogBox.isVisible()){
                hrWalked = true;
                gamePanel.getNpcmanager().hr.moveTo(-320);
            }
        }

        h = this.gamePanel.timeManager.getHours();

        if (!banIpLogTriggeredAtFive && h >= 5.0) {
            minigameManager.setTask("baniplog");
            banIpLogTriggeredAtFive = true;
            lastH = h;
            return;
        }

        if (h != lastH) {
            if ((h + 0.7) % 1.2 == 0 && !taskList.isEmpty()) {
                //settask ทีละเกมแล้วลบออก
                minigameManager.setTask(taskList.get(0));
                taskList.remove(0);
                lastH = h;
            }
        }

        if (gamePanel.getMinigameManager().taskText.equals("")) {
            if(!objList.isEmpty()){
                minigameManager.taskText = objList.getFirst();
            }
        }

        //ลบ objective ตามห้องที่ไป
        if (!objList.isEmpty()) {

            String currentRoom = gamePanel.getRoomManager().getCurrentRoomName();

            if (objList.contains(getTextFromRoom(currentRoom))) {
                if(getTextFromRoom(currentRoom).equals(minigameManager.taskText)){
                    minigameManager.taskText = "";
                }
                objList.remove(getTextFromRoom(currentRoom));

            }
        }

        if (minigameManager.afterMinigameDialogue && !startedDialogue2 ) {
            startedDialogue2 = true;
            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY1_SERVER);
            try {
                gamePanel.textBook.update();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // ดักเวลาก่อน 6 โมงเช้า
        if (h >= 5.9 && !endCgTriggered) {
            endCgTriggered = true;
            isPlayingEndCG = true;

            gamePanel.getSound().stopSound("bg1");
            gamePanel.getSound().loopSound("bg_dayEnd");

            gamePanel.timeManager.setPaused(true); // หยุดเวลาก่อน
            gamePanel.showingEnding = true;        // เข้าโหมดโชว์ CG

            // เรียกใช้รูปห้องนอน
            gamePanel.gameEnding.startEnding("CG3-BedRoom", StoryDialog.DAY1_BEDROOM);
            return;
        }
    }

    @Override
    public void draw(Graphics2D g) {}

    public void setupNPC(){
        gamePanel.getNpcmanager().showAllNPCs();

        gamePanel.getNpcmanager().janitor.setLocation("art",1600);
        gamePanel.getNpcmanager().hr.setLocation("office",1300);
        gamePanel.getNpcmanager().boss.setLocation("chiefoffice",1600);
        gamePanel.getNpcmanager().itsupport.setLocation("itsupport",1400);

        gamePanel.getInputManager().talkedToJanitor = false;
        gamePanel.getInputManager().talkedToHR = false;
        gamePanel.getInputManager().talkedToBoss=false;
        gamePanel.getInputManager().talkedToIT=false;
    }

    public void setSpecialTask(){
        gamePanel.getMinigameManager().taskBoss = true;
        gamePanel.getMinigameManager().taskJanitor = true;
    }

    //หาชประโยตจาก ชื่อmap
    private String getTextFromRoom(String obj) {
        switch (obj) {
            case "chiefoffice": return "ไปพบหัวหน้าที่ห้อง Chief Office";
            case "itsupport": return "ไปพบเพื่อนร่วมงานที่ห้อง IT Support";
            case "server": return "ไปห้อง Server Room";
            case "art": return "ไปห้อง Art Department";
        }
        return null;
    }
}