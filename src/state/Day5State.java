package state;

import core.GamePanel;
import network.MinigameManager;

import java.awt.*;
import java.util.ArrayList;

public class Day5State extends AbstractState{
    public GamePanel gamePanel;
    public MinigameManager minigameManager;

    private double h;
    private double lastH; //เพื่อเช็คว่าคำสั่งนั้นถูกเรียกใช้ครั้งที่แล้วเมื่อใด
    private ArrayList<String> taskList = new ArrayList<>();;
    private boolean banIpLogTriggeredAtFive = false;
    private boolean startedDialogue = false;
    private boolean startedEnding = false;

    public Day5State(GamePanel gamePanel) {
        //ไว้จัดฉาก เตรียมสิ่งต่าง ๆ เช่นโหลดภาพ CG เริ่มวัน หรือเอา NPC มาวางรอไว้
        this.gamePanel = gamePanel;
        minigameManager = gamePanel.getMinigameManager();

        setupNPC();
        setSpecialTask();

        gamePanel.getPlayer().xDelta=100;
        gamePanel.getPlayer().checkRight=true;

        //task ในวันนี้
        taskList.add("lan");
        taskList.add("terminal");
        taskList.add("lan");
        taskList.add("terminal");

        minigameManager.resetTask();
    }

    @Override
    public void update() {
        //เขียนเงื่อนไขดักเหตุการณ์ประจำวัน เช่น "ถ้าเวลาในเกมเดินถึงตี 2 ให้ทริกเกอร์ไฟดับ"
        if (!startedDialogue) {
            startedDialogue = true;
            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY5_LIFT1);
        }

        h = this.gamePanel.timeManager.getHours();

        if (gamePanel.getInputManager().progressDay5 == 0 && h>=3) {
            gamePanel.getInputManager().capture();
        }

        if (gamePanel.getInputManager().progressDay5 == 2&&!startedEnding) {
            if (gamePanel.dialogBox == null || !gamePanel.dialogBox.isVisible()){
                startedEnding = true;
                gamePanel.getGSM().checkEndGame();
            }
        }

        if (!banIpLogTriggeredAtFive && h >= 5.0) {
            minigameManager.setTask("baniplog");
            banIpLogTriggeredAtFive = true;
            lastH = h;
            return;
        }


        if (h != lastH) {

            if((h+1)%1.5 == 0  && !taskList.isEmpty()){
                //settask ทีละเกมแล้วลบออก
                minigameManager.setTask(taskList.getFirst());
                taskList.remove(taskList.getFirst());

                lastH = h;
                System.out.println(taskList.size());
            }


        }

    }

    @Override
    public void draw(Graphics2D g) {
        //เอาไว้ วาดภาพหรือใส่ UI พิเศษ ที่มีเฉพาะวันนั้น
    }

    public void setupNPC(){
        gamePanel.getNpcmanager().showAllNPCs();

        gamePanel.getNpcmanager().janitor.setLocation("restroom",758);
        gamePanel.getNpcmanager().hr.setLocation(null,1100);
        gamePanel.getNpcmanager().boss.setLocation("lift1",800);
        gamePanel.getNpcmanager().itsupport.setLocation("itsupport",2500);
    }

    public void setSpecialTask(){
        gamePanel.getMinigameManager().taskBoss = true;
        gamePanel.getMinigameManager().taskJanitor = true;
    }
}
