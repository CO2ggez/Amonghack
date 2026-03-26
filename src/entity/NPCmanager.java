package entity;

import map.RoomManager;

import java.awt.*;

public class NPCmanager {
    RoomManager roomManager;
    String currentRoom;
    NPC hr = new HR();
    NPC janitor = new Janitor();
    NPC itsupport = new ITSupport();
    NPC boss = new Boss();
    NPC cat = new Cat();

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
}
