package entity;

import java.awt.*;
import map.RoomManager;

public class NPCmanager {
    RoomManager roomManager;
    String currentRoom;

    public NPC hr = new HR();
    public NPC janitor = new Janitor();
    public NPC itsupport = new ITSupport();
    public NPC boss = new Boss();
    public NPC cat = new Cat();

    NPC[] npcs = new NPC[5];


    public NPCmanager(RoomManager roomManager) {
        this.roomManager = roomManager;
        currentRoom = roomManager.getCurrentRoomName();

        npcs[0] = hr;
        npcs[1] = itsupport;
        npcs[2] = boss;
        npcs[3] = cat;
        npcs[4] = janitor;

        //เรียกใช้หลักๆ 2 method นี้
        //janitor.setLocation("lift1",1500);
        //janitor.moveTo(100); //ใช้ได้้แค่ในห้องเดิม

    }

    public void drawNPC(Graphics g,int cameraX) {
        for (NPC npc : npcs) {
            if(currentRoom.equals(npc.inRoom)) {
                npc.show();
                npc.draw(g,cameraX);
            } else  {
                npc.hide();
            }

        }

    }

    public void updateNPC() {
        currentRoom = roomManager.getCurrentRoomName();

        for (NPC npc : npcs) {
            if(currentRoom.equals(npc.inRoom)) {
                npc.update();
            }

        }
    }

    public void showAllNPCs() {
        for (NPC npc : npcs) {
            npc.show();
        }
    }

    public void hideAllNPCs() {
        for (NPC npc : npcs) {
            npc.hide();
        }
    }

}
