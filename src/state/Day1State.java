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
}