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
        taskList.add("lan");
        taskList.add("terminal");

        objList.add("ไปห้องหัวหน้า");
        objList.add("ไปห้อง it support");
        objList.add("ไปห้อง server");
        objList.add("ไปห้อง art");

        minigameManager.resetTask();
    }

    @Override
    public void update() {

        //เล่น cgแรก
        if (startedEnding) {
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

                cgIndex++;

                if (cgIndex < cgList.length) {
                    gamePanel.gameEnding.startEnding(cgList[cgIndex], dialogList[cgIndex]);
                } else {
                    //เริ่มเดินเวลา ตอนcg หมด
                    gamePanel.showingEnding = false;
                    gamePanel.timeManager.setPaused(false);
                }
            }

            return;
        }

        if (!startedDialogue) {
            startedDialogue = true;
            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY1_LIFT1);
        }

        h = this.gamePanel.timeManager.getHours();

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

        if (gamePanel.getMinigameManager().taskText.equals("")) {
            if(!objList.isEmpty()){
                minigameManager.taskText = objList.getFirst();
            }
        }

        //ลบ objective ตามห้องที่ไป
        if (!objList.isEmpty()) {

            String currentObj = objList.get(0);
            String targetRoom = getRoomFromObjective(currentObj);
            String currentRoom = gamePanel.getRoomManager().getCurrentRoomName();

            if (targetRoom != null && targetRoom.equals(currentRoom)){
                if(currentObj.equals(minigameManager.taskText)){
                    minigameManager.taskText = "";
                }
                objList.remove(0);

            }
        }


    }

    @Override
    public void draw(Graphics2D g) {}

    public void setupNPC(){
        gamePanel.getNpcmanager().showAllNPCs();

        gamePanel.getNpcmanager().janitor.setLocation("art",1600);
        gamePanel.getNpcmanager().hr.setLocation("office",1100);
        gamePanel.getNpcmanager().boss.setLocation("chiefoffice",1600);
        gamePanel.getNpcmanager().itsupport.setLocation("itsupport",1400);
    }

    public void setSpecialTask(){
        gamePanel.getMinigameManager().taskBoss = true;
        gamePanel.getMinigameManager().taskJanitor = true;
    }

    //หาชื่อห้องจากประโยคobjective
    private String getRoomFromObjective(String obj) {
        switch (obj) {
            case "ไปห้องหัวหน้า": return "chiefoffice";
            case "ไปห้อง it support": return "itsupport";
            case "ไปห้อง server": return "server";
            case "ไปห้อง art": return "art";
        }
        return null;
    }
}