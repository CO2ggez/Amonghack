package entity;

import map.RoomManager;

import java.awt.*;

public class NPCmanager {
    RoomManager roomManager;
    String currentRoom;
    NPC hr = new HR();

    NPC[] npcs = new NPC[1];

    public NPCmanager(RoomManager roomManager) {
        this.roomManager = roomManager;
        currentRoom = roomManager.getCurrentRoomName();

        npcs[0] = hr;
    }

    public void drawNPC(Graphics g) {
        for (NPC npc : npcs) {
            if(currentRoom.equals(npc.inRoom)) {
                npc.show();
                npc.draw(g);
            } else  {
                npc.hide();
            }

        }

    }

    public void updateNPC() {
        currentRoom = roomManager.getCurrentRoomName();
        hr.update();
    }
}
