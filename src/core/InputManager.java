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
import org.w3c.dom.Text;
import ui.Camera;


import event.EventManager;
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

    // ตัวแปรเก็บความคืบหน้าของเหตุการณ์ที่ต้องทำต่อเนื่อง (Day 4 และ 5)
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


    public InputManager(Camera camera, RoomManager roomManager, GamePanel panel, Player player, MinigameManager minigameManager, NPCmanager npcManager,TextBook tb) {




        this.gamePanel = panel;
        this.camera = camera;
        this.roomManager = roomManager;
        this.player = player;
        this.minigameManager = minigameManager;

        this.npcManager = npcManager; 

        this.textBook=tb;



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
                    roomManager.changeFloor(roomManager.mapDataFloor1, "stair1", 1400);
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

            if (roomManager.getCurrentRoomName().equals("server") && isNear(720, 1180)) {
                minigameManager.startTask();
                return;
            }

            if ((roomManager.getCurrentRoomName().equals(minigameManager.currentLanLocation[0])) &&
                isNear(Integer.parseInt(minigameManager.currentLanLocation[1]), Integer.parseInt(minigameManager.currentLanLocation[2])) 
                && minigameManager.taskLan) {
                minigameManager.startTask();
                return;
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

        //ระบบเนื้อเรื่อง (กด E)
        if (e.getKeyCode() == KeyEvent.VK_E) {
            if (gamePanel != null && gamePanel.dialogBox != null && !gamePanel.dialogBox.isVisible()) {
                
                //ดึงข้อมูลวันปัจจุบันมาเช็ค
                int actualDay = 1;
                if (gamePanel.getGSM() != null) {
                    actualDay = gamePanel.getGSM().getCurrentDay();
                }

                //ถ้าเปลี่ยนวัน ให้รีเซ็ตสถานะการคุยเป็นยังไม่ได้คุยทั้งหมด
                if (actualDay != currentTrackedDay) {
                    currentTrackedDay = actualDay;
                    talkedToBoss = false;
                    talkedToHR = false;
                    talkedToIT = false;
                    talkedToJanitor = false;
                    talkedToServer = false;
                    progressDay4 = 0;
                    progressDay5 = 0;
                }

                String room = roomManager.getCurrentRoomName();
                
                // --- เหตุการณ์ DAY 1 ---
                if (actualDay == 1) {
                    if (bossArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.INTERVIEW);
                        talkedToBoss = true; 
                    } 
                    else if (hrArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY1_HR);
                        talkedToHR = true;
                    } 
                    else if (itsupportArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY1_ITSUP);
                        talkedToIT = true;
                    } 
                    else if (janitorArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY1_JANITOR);
                        talkedToJanitor = true;
                    } 
                    else if (room.equals("server") && !talkedToServer && isNear(500, 800)) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY1_SERVER);
                        talkedToServer = true;
                    }
                }
                // --- เหตุการณ์ DAY 2 ---
                else if (actualDay == 2) {
                    if (bossArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY2_BOSS_CALL);
                        talkedToBoss = true; 
                    } 
                    else if (hrArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY2_HR);
                        talkedToHR = true;
                    } 
                    else if (itsupportArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY2_ITSUP);
                        talkedToIT = true;
                    } 
                    else if (room.equals("server") && !talkedToServer && isNear(500, 800)) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY2_SERVER);
                        talkedToServer = true;
                    }
                }
                // --- เหตุการณ์ DAY 3 ---
                else if (actualDay == 3) {
                    if (bossArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY3_BOSS);
                        talkedToBoss = true; 
                    } 
                    else if (janitorArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY3_POWEROUT);
                        talkedToJanitor = true;
                    } 
                    else if (room.equals("server") && !talkedToServer && isNear(500, 800)) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY3_SERVER);
                        talkedToServer = true;
                    }
                }
                // --- เหตุการณ์ DAY 4 ---
                else if (actualDay == 4) {
                    if (bossArea() && progressDay4 == 0) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY4_BOSS_1);
                        progressDay4 = 1; // อัปเดตเพื่อให้ไป Step ถัดไปได้
                    } 
                    else if (room.equals("server") && progressDay4 == 1 && isNear(500, 800)) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY4_BRIDGE);
                        progressDay4 = 2;
                    } 
                    else if (itsupportArea()) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY4_ITSUP);
                        progressDay4 = 3;
                    } 
                    else if (bossArea() && progressDay4 == 3) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY4_BOSS_2);
                        progressDay4 = 4;
                    }
                }
                // --- เหตุการณ์ DAY 5 + ENDING (จับกุม และ Ending) ---
                else if (actualDay == 5) {
                    if (room.equals(npcManager.janitor.inRoom) && progressDay5 == 0 && isNear(200, 500)) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.OFFICE_DAY5_CLIMAX);
                        progressDay5 = 1; 
                    } 
                    else if (bossArea() && progressDay5 == 1) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.ENDING_BOSS);
                        progressDay5 = 2;
                    } 
                    else if (janitorArea() && progressDay5 == 2) {
                        gamePanel.dialogBox.startDialog(ui.StoryDialog.ENDING_JANITOR);
                        progressDay5 = 3;
                    }
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            interact();
        }


        //textbookdraw
        if (e.getKeyCode()==KeyEvent.VK_M){
            textBook.setVisible(!textBook.isVisible());
        }
        //จบโค้ดที่มาจาก Player----------------------------

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) player.rightPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_A) player.leftPressed = false;
        player.moving = player.leftPressed || player.rightPressed; 
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

        if (room.equals("server") && isNear(720, 1180)) return "[F] Use terminal";

        //เช็คHintของระบบเนื้อเรื่อง
        int actualDay = 1;
        if (gamePanel.getGSM() != null) {
            actualDay = gamePanel.getGSM().getCurrentDay();
        }

        if (actualDay == 1) {
            if (bossArea()) return "[E] คุยกับบอส";
            if (room.equals(npcManager.hr.inRoom) && !talkedToHR && isNear(300, 600)) return "[E] คุยกับ HR"; 
            if (itsupportArea()) return "[E] คุยกับ IT Support";
            if (room.equals(npcManager.janitor.inRoom) && !talkedToJanitor && isNear(200, 500)) return "[E] คุยกับภารโรง"; 
            if (room.equals("server") && !talkedToServer && isNear(500, 800)) return "[E] สำรวจ"; 
        } 
        else if (actualDay == 2) {
            if (bossArea()) return "[E] โทรศัพท์จากบอส";
            if (hrArea()) return "[E] คุยกับ HR";
            if (itsupportArea()) return "[E] คุยกับ IT Support";
            if (room.equals("server") && !talkedToServer && isNear(500, 800)) return "[E] สำรวจ"; 
        }
        else if (actualDay == 3) {
            if (bossArea()) return "[E] คุยกับบอส";
            if (janitorArea()) return "[E] คุยกับภารโรง";
            if (room.equals("server") && !talkedToServer && isNear(500, 800)) return "[E] สำรวจ"; 
        }
        else if (actualDay == 4) {
            if (bossArea() && progressDay4 == 0) return "[E] คุยกับบอส";
            if (room.equals("server") && progressDay4 == 1 && isNear(500, 800)) return "[E] สำรวจ Network Bridge"; 
            if (itsupportArea()) return "[E] คุยกับ IT Support";
            if (bossArea() && progressDay4 == 3 ) return "[E] รายงานบอส";
        }
        else if (actualDay == 5) {
            if (janitorArea() && progressDay5 == 0) return "[E] เข้าจับกุม!";
            if (bossArea()&& progressDay5 == 1) return "[E] ดื่มฉลอง";
            if (janitorArea() && progressDay5 == 2) return "[E] เยี่ยมเยียน";
        }

        return null;
    }

    public boolean bossArea(){
        return (roomManager.getCurrentRoomName().equals(npcManager.boss.inRoom) && !talkedToBoss && isNear(npcManager.boss.getX(),npcManager.boss.getX()+300));
    }

    public boolean janitorArea(){
        return (roomManager.getCurrentRoomName().equals(npcManager.janitor.inRoom) && !talkedToJanitor && isNear(npcManager.janitor.getX(),npcManager.janitor.getX()+300));
    }
    public boolean hrArea(){
        return (roomManager.getCurrentRoomName().equals(npcManager.hr.inRoom) && !talkedToHR && isNear(npcManager.hr.getX(),npcManager.hr.getX()+300));
    }
    public boolean itsupportArea(){
        return (roomManager.getCurrentRoomName().equals(npcManager.itsupport.inRoom) && !talkedToIT && isNear(npcManager.itsupport.getX(),npcManager.itsupport.getX()+300));
    }


}