package state;

import core.GamePanel;
import network.MinigameManager;
import ui.StoryDialog;

import java.awt.*;
import java.util.ArrayList;

public class Day4State extends AbstractState {
    public GamePanel gamePanel;
    public MinigameManager minigameManager;

    private double h;
    private double lastH; //เพื่อเช็คว่าคำสั่งนั้นถูกเรียกใช้ครั้งที่แล้วเมื่อใด
    private ArrayList<String> taskList = new ArrayList<>();
    private boolean banIpLogTriggeredAtFive = false;

    // จัดการ CG คั่นก่อนจบวัน
    private boolean isPlayingEndCG = false;
    private boolean endCgTriggered = false;

    private boolean startedDialoguelog = false;

    private ArrayList<String> objList = new ArrayList<>();

    public Day4State(GamePanel gamePanel) {
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
        taskList.add("lan");
        taskList.add("terminal");
        taskList.add("lan");
        taskList.add("terminal");

        minigameManager.resetTask();

        // มั่นใจว่าเริ่มวันมาต้องไม่ค้างหน้าจอ CG
        gamePanel.showingEnding = false;
        gamePanel.timeManager.setPaused(false);

        minigameManager.afterMinigameDialogue=false;

        objList.add("ตรวจตู้ Server");
        objList.add("รายงานหัวหน้า");
        objList.add("คุยกับทุกคน");
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

            if((h+1)%1.5 == 0  && !taskList.isEmpty()){
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

            gamePanel.getSound().stopSound("bg1");
            gamePanel.getSound().setVolume("bg_dayEnd", 0.5f);
            gamePanel.getSound().loopSound("bg_dayEnd");

            gamePanel.timeManager.setPaused(true); // หยุดเวลาก่อน
            gamePanel.showingEnding = true;        // เข้าโหมดโชว์ CG

            // เรียกใช้รูปห้องนอน
            gamePanel.gameEnding.startEnding("CG3-BedRoom", StoryDialog.DAY4_BEDROOM);
            return;
        }

        //dialogue หลังเช็คlog
        if (minigameManager.afterMinigameDialogue && !startedDialoguelog) {
            startedDialoguelog = true;
            gamePanel.dialogBox.startDialog(StoryDialog.DAY4_LOG);
            try { gamePanel.textBook.update(); } catch (Exception ex) { ex.printStackTrace(); }
        }

        //objective text
        if (gamePanel.getMinigameManager().taskText.equals("")) {
            if(!objList.isEmpty()){
                minigameManager.taskText = objList.getFirst();
            }
        }

        if (!objList.isEmpty()){
            if((gamePanel.getInputManager().progressDay4==2 &&objList.getFirst().equals("ตรวจตู้ Server"))||
                    (gamePanel.getInputManager().progressDay4==3 &&objList.getFirst().equals("รายงานหัวหน้า"))){
                if(objList.getFirst().equals(minigameManager.taskText)){
                    minigameManager.taskText = "";
                }objList.remove(0);

                if(gamePanel.getInputManager().progressDay4==2){
                    gamePanel.getNpcmanager().boss.setLocation("chiefoffice",1600);
                }
            } else if ((gamePanel.getInputManager().talkedToBoss && gamePanel.getInputManager().talkedToHR
                    && gamePanel.getInputManager().talkedToIT && gamePanel.getInputManager().talkedToJanitor
            && objList.contains("คุยกับทุกคน"))) {
                objList.remove("คุยกับทุกคน");
            }
        }


    }

    @Override
    public void draw(Graphics2D g) {
        //เอาไว้ วาดภาพหรือใส่ UI พิเศษ ที่มีเฉพาะวันนั้น
    }

    public void setupNPC(){
        gamePanel.getNpcmanager().showAllNPCs();

        gamePanel.getNpcmanager().janitor.setLocation("restroom",2430);
        gamePanel.getNpcmanager().hr.setLocation("office",1100);
        gamePanel.getNpcmanager().boss.setLocation("lift1",230);
        gamePanel.getNpcmanager().itsupport.setLocation("itsupport",1444);
    }

    public void setSpecialTask(){
        gamePanel.getMinigameManager().taskBoss = true;
        gamePanel.getMinigameManager().taskJanitor = true;
    }
}
