package core;

import entity.NPCmanager;
import entity.Player;
import event.EventManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import map.RoomManager;
import network.MinigameManager;
import ui.Camera;
import ui.TextBook;

public class InputManager implements KeyListener {
    private Camera camera;
    private RoomManager roomManager;
    private GamePanel gamePanel;
    private Player player;
    private MinigameManager minigameManager;
    private NPCmanager npcManager; 

    // --- ตัวแปรสำหรับจัดการเนื้อเรื่อง ---
    private int currentTrackedDay = 1;
    
    // สถานะว่าคุยกับใครไปแล้วบ้าง (จะถูกรีเซ็ตเมื่อเปลี่ยนวัน)
    private boolean talkedToBoss = false;
    private boolean talkedToHR = false;
    private boolean talkedToIT = false;
    private boolean talkedToJanitor = false;
    private boolean talkedToServer = false;

    // ตัวแปรเก็บความคืบหน้าของเหตุการณ์ที่ต้องทำต่อเนื่องในแต่ละวัน
    private int progressDay2 = 0;
    private int progressDay3 = 0;
    private int progressDay4 = 0;
    private int progressDay5 = 0;

    // --- Helper Method เช็คระยะห่างว่าอยู่ใกล้ไหม ---
    private boolean isNear(int minX, int maxX) {
        return player.xDelta >= minX && player.xDelta <= maxX;
    }

    private TextBook textBook;

    public void interact() {
        if (gamePanel != null && gamePanel.dialogBox != null && gamePanel.dialogBox.isVisible()) {
            gamePanel.dialogBox.nextText();
        }
    }

    public InputManager(Camera camera, RoomManager roomManager, GamePanel panel, Player player, MinigameManager minigameManager, NPCmanager npcManager, TextBook tb) {
        this.gamePanel = panel;
        this.camera = camera;
        this.roomManager = roomManager;
        this.player = player;
        this.minigameManager = minigameManager;
        this.npcManager = npcManager; 
        this.textBook = tb;

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                interact();
                int mouseX = e.getX();
                int mouseY = e.getY();
                EventManager em = gamePanel.getEventManager();

                if (em != null && em.isShowImage() && em.getActiveZoneName().equals("Elevator_Panel")) {
                    if (mouseX >= 906 && mouseX <= 1026 && mouseY >= 318 && mouseY <= 438) {
                        gamePanel.startTransition(() -> {
                            roomManager.changeFloor(roomManager.mapDataFloor2, "lift2", 500);
                            em.closeEvent(); 
                        });
                        return; 
                    }
                    else if (mouseX >= 906 && mouseX <= 1026 && mouseY >= 480 && mouseY <= 606) {
                        gamePanel.startTransition(() -> {
                            roomManager.changeFloor(roomManager.mapDataFloor1, "lift1", 500);
                            em.closeEvent();
                        });
                        return;
                    }
                    else if (mouseX >= 906 && mouseX <= 1026 && mouseY >= 648 && mouseY <= 768) {
                        gamePanel.startTransition(() -> {
                            roomManager.changeFloor(roomManager.mapDataFloorG, "liftG", 500);
                            em.closeEvent();
                        });
                        return;
                    }
                    em.closeEvent();
                    return;
                }

                if (minigameManager != null && minigameManager.isPlaying()) {
                    return;
                }
                int worldX = mouseX + camera.getX();
                if (em != null) {
                    String currentRoom = roomManager.getCurrentRoomName();
                    em.checkClick(worldX, mouseY, currentRoom);
                }
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        // [แก้ไข] ล็อกการเดินและการกดปุ่มอื่นๆ ทั้งหมดขณะมีกล่องข้อความแสดงอยู่
        if (gamePanel != null && gamePanel.dialogBox != null && gamePanel.dialogBox.isVisible()) {
            player.leftPressed = false;
            player.rightPressed = false;
            player.moving = false;
            
            // อนุญาตให้กดแค่ปุ่ม Spacebar เพื่ออ่านข้อความถัดไป
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                interact();
            }
            return; // ยกเลิกการทำงานของปุ่มอื่นๆ ที่เหลือ
        }

        //ไว้กด N บังคับเปลี่ยนวัน
        if (e.getKeyCode() == KeyEvent.VK_N) {
            if (gamePanel.getIsTransitioning()) {
                gamePanel.startNextDay();
            } else {
                gamePanel.timeManager.forceEndDay();
            }

            player.leftPressed = false;
            player.rightPressed = false;
            player.moving = false;
            return;
        }
        
        // ---------------- ระบบเคลื่อนย้ายฉากและเกม ----------------
        if (e.getKeyCode() == KeyEvent.VK_F) {
            String roomName = roomManager.getCurrentRoomName();

            if (roomName.equals("stairG") && isNear(577, 864)) {
                gamePanel.startTransition(() -> {
                    roomManager.changeFloor(roomManager.mapDataFloor1, "stair1", 500);
                });
                return;
            }

            if (roomName.equals("stair1")) {
                if (isNear(577, 864)) {
                    gamePanel.startTransition(() -> {
                        roomManager.changeFloor(roomManager.mapDataFloor2, "stair2", 500);
                    });
                    return;
                }
                if (isNear(288, 576)) {
                    gamePanel.startTransition(() -> {
                        roomManager.changeFloor(roomManager.mapDataFloorG, "stairG", 500);
                    });
                    return;
                }
            }

            if (roomName.equals("stair2") && isNear(288, 576)) {
                gamePanel.startTransition(() -> {
                    roomManager.changeFloor(roomManager.mapDataFloor1, "stair1", 500);
                });
                return; 
            }

            if (player.xDelta <= 40) {
                gamePanel.startTransition(() -> {
                    roomManager.changeRoomLeft(camera);
                });
                return;
            }

            if (player.xDelta >= roomManager.getWidth() - 150) {
                gamePanel.startTransition(() -> {
                    roomManager.changeRoomRight(camera);
                });
                return;
            }

            if ((roomManager.getCurrentRoomName().equals(minigameManager.currentLanLocation[0])) &&
                isNear(Integer.parseInt(minigameManager.currentLanLocation[1]), Integer.parseInt(minigameManager.currentLanLocation[2])) 
                && minigameManager.taskLan) {
                minigameManager.startTask();
                return;
            }
            if ((roomManager.getCurrentRoomName().equals(minigameManager.currentTerminalLocation[0])) &&
                    isNear(Integer.parseInt(minigameManager.currentTerminalLocation[1]), Integer.parseInt(minigameManager.currentTerminalLocation[2]))
                    && minigameManager.taskTerminal) {
                minigameManager.startTask();
                return;
            }

            //task boss, janitor
            if(helpBossArea()){
                minigameManager.taskBoss = false;
                minigameManager.helpBossScore++;
                gamePanel.showNotification("จัดเอกสารเรียบร้อยแล้ว");
            }

            if(helpJanitorArea()){
                minigameManager.taskJanitor = false;
                minigameManager.helpJanitorScore++;
                gamePanel.showNotification("ไม้กวาดถูกเก็บเข้าตู้แล้ว");
            }
        } 

        if (gamePanel.getIsTransitioning()) {
            gamePanel.startNextDay();
            player.leftPressed = false;
            player.rightPressed = false;
            player.moving = false;
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            player.checkRight = true;
            player.rightPressed = true; 
            player.moving = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            player.checkRight = false;
            player.leftPressed = true;
            player.moving = true;
        }

        // ---------------- ระบบเนื้อเรื่อง (กด E) ----------------
        if (e.getKeyCode() == KeyEvent.VK_E) {
            if (gamePanel != null && gamePanel.dialogBox != null && !gamePanel.dialogBox.isVisible()) {
                
                // [แก้ไข] บังคับหยุดเดินทันทีเมื่อเริ่มกดคุย
                player.leftPressed = false;
                player.rightPressed = false;
                player.moving = false;
                
                int actualDay = 1;
                if (gamePanel.getGSM() != null) {
                    actualDay = gamePanel.getGSM().getCurrentDay();
                }

                // รีเซ็ตตัวแปรเมื่อเปลี่ยนวัน
                if (actualDay != currentTrackedDay) {
                    currentTrackedDay = actualDay;
                    talkedToBoss = false;
                    talkedToHR = false;
                    talkedToIT = false;
                    talkedToJanitor = false;
                    talkedToServer = false;
                    progressDay2 = 0;
                    progressDay3 = 0;
                    progressDay4 = 0;
                    progressDay5 = 0;
                }

                String room = roomManager.getCurrentRoomName();
                
                // --- ใช้ switch case แทน if-else สำหรับเช็ควัน ---
                switch (actualDay) {
                    case 1:
                        if (bossArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY1_CHIEFOFFICE);
                            talkedToBoss = true; 
                        } else if (hrArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY1_OFFICE);
                            talkedToHR = true;
                        } else if (janitorArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY1_ART);
                            talkedToJanitor = true;
                        } else if (itsupportArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY1_ITSUPPORT);
                            talkedToIT = true;
                        } else if (room.equals("server") && !talkedToServer && isNear(500, 800)) { 
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY1_SERVER);
                            talkedToServer = true;
                        }
                        break;
                        
                    case 2:
                        if (bossArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY2_LIFT1);
                            talkedToBoss = true; 
                        } else if (hrArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY2_OFFICE);
                            talkedToHR = true;
                        } else if (janitorArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY2_MEETING); 
                            talkedToJanitor = true;
                        } else if (itsupportArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY2_ITSUPPORT);
                            talkedToIT = true;
                        } else if (room.equals("office") && progressDay2 == 0 && isNear(400, 600)) { 
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY2_OFFICE_CHECK); 
                            progressDay2 = 1;
                        } else if (room.equals("server") && progressDay2 == 1 && isNear(500, 800)) { 
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY2_SERVER); 
                            progressDay2 = 2;
                        }
                        break;
                        
                    case 3:
                        if (bossArea()) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY3_LIFT1);
                            talkedToBoss = true; 
                        
                        } else if (itsupportArea() && progressDay3 == 0) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY3_ITSUPPORT_PART1);
                            progressDay3 = 1;

                            new Thread(() -> {
                                try {
                                    while(gamePanel.dialogBox.isVisible()) Thread.sleep(100);
                                    
                                    npcManager.boss.inRoom = "itsupport";
                                    
                                    int startX = 0; 
                                    int targetX = player.xDelta - 100;
                                    
                                    npcManager.boss.walkSpeed = 15;
                                    npcManager.boss.x = startX;
                                    npcManager.boss.moveTo(targetX);
                                    
                                    while(npcManager.boss.getX() != targetX) {
                                        Thread.sleep(50);
                                    }
                                    
                                    gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY3_ITSUPPORT_PART2);
                                    progressDay3 = 2;
                                    
                                } catch (Exception ex) { ex.printStackTrace(); }
                            }).start();

                        } else if (room.equals("liftG") && progressDay3 == 2 && isNear(200, 500)) { 
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY3_LIFTG); 
                            progressDay3 = 3;
                        } else if (room.equals("server") && progressDay3 == 3 && isNear(500, 800)) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY3_SERVER); 
                            progressDay3 = 4;
                        }
                        break;
                        
                    case 4:
                        if (bossArea() && progressDay4 == 0) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY4_LIFT1); 
                            progressDay4 = 1;
                        } else if (room.equals("meeting") && progressDay4 == 1 && isNear(600, 900)) { 
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY4_SERVER); 
                            progressDay4 = 2;
                        } else if (bossArea() && progressDay4 == 2) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY4_CHIEFOFFICE); 
                            progressDay4 = 3;
                        }
                        break;
                        
                    case 5:
                        if (room.equals("restroom") && progressDay5 == 0 && isNear(200, 500)) { 
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY5_RESTROOM_PART1);
                            progressDay5 = 1; 

                            new Thread(() -> {
                                try {
                                    while(gamePanel.dialogBox.isVisible()) Thread.sleep(100);
                                    
                                    npcManager.boss.inRoom = "restroom";
                                    npcManager.itsupport.inRoom = "restroom";
                                    
                                    int startX = 0; 
                                    int targetX = player.xDelta - 100;
                                    
                                    npcManager.boss.walkSpeed = 15;
                                    npcManager.itsupport.walkSpeed = 15;
                                    
                                    npcManager.boss.x = startX;
                                    npcManager.itsupport.x = startX - 80;
                                    
                                    npcManager.boss.moveTo(targetX);
                                    npcManager.itsupport.moveTo(targetX - 80);
                                    
                                    while(npcManager.boss.getX() != targetX || npcManager.itsupport.getX() != (targetX - 80)) {
                                        Thread.sleep(50);
                                    }
                                    
                                    gamePanel.dialogBox.startDialog(ui.StoryDialog.DAY5_RESTROOM_PART2);
                                    progressDay5 = 2;

                                } catch (Exception ex) { ex.printStackTrace(); }
                            }).start();

                        } else if (bossArea() && progressDay5 == 2) {
                            gamePanel.dialogBox.startDialog(ui.StoryDialog.ENDING_CHIEF); 
                            progressDay5 = 3;
                        } 
                        break;
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            interact();
        }

        //textbook draw
        if (e.getKeyCode() == KeyEvent.VK_M){
            textBook.setVisible(!textBook.isVisible());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) player.rightPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_A) player.leftPressed = false;
        
        // [แก้ไข] อัปเดตสถานะการเดินก็ต่อเมื่อไม่มีกล่องข้อความเท่านั้น
        if (gamePanel == null || gamePanel.dialogBox == null || !gamePanel.dialogBox.isVisible()) {
            player.moving = player.leftPressed || player.rightPressed; 
        }
    }

    public MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                interact();
            }
        };
    }

    public String getCurrentHint() {
        String room = roomManager.getCurrentRoomName();

        if ((player.xDelta <= 40 && roomManager.haveRoomLeft()) || (player.xDelta >= roomManager.getWidth() - 150 && roomManager.haveRoomRight())){
            return "[F] ไปห้องถัดไป";
        }

        if (room.startsWith("stair")) {
            if (isNear(288, 576)) {
                if (room.equals("stair1")) return "[F] ไปชั้นล่าง";
                if (room.equals("stair2")) return "[F] ไปชั้นล่าง";
            }
            if (isNear(577, 864)) {
                if (room.equals("stairG") || room.equals("stair1")) return "[F] ไปชั้นบน";
            }
        }

        if ((roomManager.getCurrentRoomName().equals(minigameManager.currentLanLocation[0])) &&
             isNear(Integer.parseInt(minigameManager.currentLanLocation[1]), Integer.parseInt(minigameManager.currentLanLocation[2])) 
             && minigameManager.taskLan) {
            return "[F] เชื่อมสายแลน";
        }

        if ((roomManager.getCurrentRoomName().equals(minigameManager.currentTerminalLocation[0])) &&
                isNear(Integer.parseInt(minigameManager.currentTerminalLocation[1]), Integer.parseInt(minigameManager.currentTerminalLocation[2]))
                && minigameManager.taskTerminal) {
            return "[F] เปิด Terminal";
        }

        //ตำแหน่งช่วยงาน
        if(helpBossArea()){
            return "[F] ช่วยจัดเอกสาร";
        }
        if(helpJanitorArea()){
            return "[F] ช่วยเก็บไม้กวาด";
        }

        // --- เช็ค Hint ของระบบเนื้อเรื่องตามวัน (เปลี่ยนมาใช้ switch แทน) ---
        int actualDay = 1;
        if (gamePanel.getGSM() != null) {
            actualDay = gamePanel.getGSM().getCurrentDay();
        }

        switch (actualDay) {
            case 1:
                if (bossArea()) return "[E] คุยกับหัวหน้า";
                if (hrArea()) return "[E] คุยกับพนักงานแผนก HR";
                if (itsupportArea()) return "[E] คุยกับ IT Support";
                if (janitorArea()) return "[E] คุยกับภารโรง";
                if (room.equals("server") && !talkedToServer && isNear(500, 800)) return "[E] ตรวจสอบ Log"; 
                break;
            case 2:
                if (bossArea()) return "[E] รับเรื่องจากหัวหน้า";
                if (hrArea()) return "[E] คุยกับพนักงานแผนก HR";
                if (itsupportArea()) return "[E] คุยกับ IT Support";
                if (janitorArea()) return "[E] คุยกับภารโรง";
                if (room.equals("office") && progressDay2 == 0 && isNear(400, 600)) return "[E] ตรวจสอบเครื่องคอมพิวเตอร์ HR"; 
                if (room.equals("server") && progressDay2 == 1 && isNear(500, 800)) return "[E] ตรวจ Log และตู้ Network"; 
                break;
            case 3:
                if (bossArea()) return "[E] คุยกับหัวหน้า";
                if (itsupportArea() && progressDay3 == 0) return "[E] คุยกับ IT Support";
                if (room.equals("liftG") && progressDay3 == 2 && isNear(200, 500)) return "[E] ตรวจสอบตู้ไฟ / คุยกับภารโรง"; 
                if (room.equals("server") && progressDay3 == 3 && isNear(500, 800)) return "[E] ตรวจ Log หลังไฟดับ"; 
                break;
            case 4:
                if (bossArea() && progressDay4 == 0) return "[E] ขออนุญาตหัวหน้าไล่สาย LAN";
                if (room.equals("meeting") && progressDay4 == 1 && isNear(600, 900)) return "[E] สำรวจพื้นที่ต้องสงสัย"; 
                if (bossArea() && progressDay4 == 2) return "[E] แจ้งหัวหน้าเรื่องอุปกรณ์แปลกปลอม";
                break;
            case 5:
                if (room.equals("restroom") && progressDay5 == 0 && isNear(200, 500)) return "[E] เข้าจับกุม!";
                if (bossArea() && progressDay5 == 2) return "[E] สรุปคดีกับหัวหน้า";
                break;
        }

        return null;
    }

    // --- เช็คว่าเราอยู่ตำแหน่ง NPC นั้นไหม ---
    public boolean bossArea(){
        return (roomManager.getCurrentRoomName().equals(npcManager.boss.inRoom) && !talkedToBoss && isNear(npcManager.boss.getX()-150, npcManager.boss.getX()+300));
    }

    public boolean janitorArea(){
        return (roomManager.getCurrentRoomName().equals(npcManager.janitor.inRoom) && !talkedToJanitor && isNear(npcManager.janitor.getX()-150, npcManager.janitor.getX()+300));
    }

    public boolean hrArea(){
        return (roomManager.getCurrentRoomName().equals(npcManager.hr.inRoom) && !talkedToHR && isNear(npcManager.hr.getX()-150, npcManager.hr.getX()+300));
    }

    public boolean itsupportArea(){
        return (roomManager.getCurrentRoomName().equals(npcManager.itsupport.inRoom) && !talkedToIT && isNear(npcManager.itsupport.getX()-150, npcManager.itsupport.getX()+300));
    }

    public boolean helpBossArea(){
        return roomManager.getCurrentRoomName().equals("chiefoffice") && isNear(190*6, 275*6) && minigameManager.taskBoss;
    }

    public boolean helpJanitorArea(){
        return roomManager.getCurrentRoomName().equals("restroom") && isNear(89*6, 143*6) && minigameManager.taskJanitor;
    }
}