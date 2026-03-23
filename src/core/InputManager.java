package core;

import entity.Player;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import map.RoomManager;
import ui.Camera;
import event.EventManager;

public class InputManager implements KeyListener {
    private Camera camera;
    private RoomManager roomManager;
    private GamePanel gamePanel;
    private Player player;

    public void interact() {
        if (gamePanel != null && gamePanel.dialogBox != null && gamePanel.dialogBox.isVisible()) {
            gamePanel.dialogBox.nextText();
    }
}

    public InputManager(Camera camera, RoomManager roomManager,GamePanel panel,Player player) {

        this.gamePanel = panel;
        this.camera = camera;
        this.roomManager = roomManager;
        this.player = player;

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("คลิกหน้าจอ!");
                interact();

                // --- เพิ่มระบบคลิก Event ---
                // 1. รับค่าเมาส์บนจอภาพ
                int mouseX = e.getX();
                int mouseY = e.getY();

                EventManager em = gamePanel.getEventManager();

                System.out.println("เมาส์คลิกที่พิกัดจอ -> X: " + mouseX + " | Y: " + mouseY);

                // 2. เช็คว่า "หน้าต่างลิฟต์เปิดอยู่ไหม?"
                if (em != null && em.isShowImage() && em.getActiveZoneName().equals("Elevator_Panel")) {

                    // --- สมมติพิกัดปุ่มบนหน้าจอ (คุณต้องปรับตัวเลขให้ตรงกับรูปปุ่มในเกมของคุณ) ---

                    // เช็คปุ่ม "ไปชั้น 2" (สมมติปุ่มอยู่ช่วง X: 800-900, Y: 300-350)
                    if (mouseX >= 849 && mouseX <= 1070 && mouseY >= 139 && mouseY <= 359) {
                        System.out.println("ลิฟต์: ไปชั้น 2");
                        // เปลี่ยนไปแมพชั้น 2, โผล่ที่ห้อง lift2, วางตัวละครที่ X=500
                        roomManager.changeFloor(roomManager.mapDataFloor2, "lift2", 500);
                        em.closeEvent(); // ปิดหน้าต่างลิฟต์
                        return; // จบการทำงานของคลิกนี้
                    }
                    // เช็คปุ่ม "ไปชั้น 1" (สมมติปุ่มอยู่ช่วง X: 800-900, Y: 400-450)
                    else if (mouseX >= 849 && mouseX <= 1070 && mouseY >= 433 && mouseY <= 662) {
                        System.out.println("ลิฟต์: ไปชั้น 1");
                        roomManager.changeFloor(roomManager.mapDataFloor1, "lift1", 500);
                        em.closeEvent();
                        return;
                    }
                    // เช็คปุ่ม "ไปชั้น G" (สมมติปุ่มอยู่ช่วง X: 800-900, Y: 500-550)
                    else if (mouseX >= 849 && mouseX <= 1070 && mouseY >= 748 && mouseY <= 954) {
                        System.out.println("ลิฟต์: ไปชั้น G");
                        roomManager.changeFloor(roomManager.mapDataFloorG, "liftG", 500);
                        em.closeEvent();
                        return;
                    }

                    // (ทางเลือกเสริม) ถ้าคลิกที่ว่างอื่นๆ นอกปุ่ม ให้ปิดหน้าต่างลิฟต์ทิ้ง
                    em.closeEvent();
                    return;
                }

                // 3. ถ้าไม่มี UI หน้าต่างใดๆ เปิดอยู่ ค่อยเช็คการคลิกในฉากเกม (World Coordinates)
                int worldX = mouseX + camera.getX();
                if (em != null) {
                    em.checkClick(worldX, mouseY);
                }
            }
        });
    }



    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        //ระบบการกด F เปลี่ยนห้องเมื่ออยู่ขอบแมพ ไม่ซ้ายก้ขวา
        if(e.getKeyCode() == KeyEvent.VK_F){

            String roomName = roomManager.getCurrentRoomName();

            // --- เงื่อนไขเปลี่ยนชั้น (เพิ่มใหม่) ---
            
            // กรณีอยู่ชั้น G จะขึ้นไปชั้น 1 (ต้องอยู่ที่ห้อง stairG และยืนใกล้บันได)
            if(roomName.equals("stairG") && player.xDelta >= 400 && player.xDelta <= 600) {
                // เปลี่ยนไป Floor 1, ห้อง stair1, วางตัวละครที่ X=500
                roomManager.changeFloor(roomManager.mapDataFloor1, "stair1", 500); 
                return;
            }
            
            // กรณีอยู่ชั้น 1 จะขึ้นไปชั้น 2 หรือลงไปชั้น G
            if(roomName.equals("stair1")) {
                if(player.xDelta >= 400 && player.xDelta <= 600) { // สมมติเป็นจุดขึ้นไปชั้น 2
                    roomManager.changeFloor(roomManager.mapDataFloor2, "stair2", 500);
                    return;
                }
                if(player.xDelta >= 100 && player.xDelta <= 300) { // สมมติเป็นจุดลงไปชั้น G
                    roomManager.changeFloor(roomManager.mapDataFloorG, "stairG", 500);
                    return;
                }
            }

            if(roomName.equals("stair2")) {
            // เช็คว่าตัวละครยืนอยู่ใกล้บันไดไหม (สมมติพิกัดบันไดอยู่ช่วงกลางแมพ)
            if(player.xDelta >= 400 && player.xDelta <= 700) { 
                // สั่งเปลี่ยนไปใช้แมพ Floor 1 / ไปโผล่ที่ห้อง stair1 / วางตัวละครที่ X=1400 (ขวาหน้าบันได)
                roomManager.changeFloor(roomManager.mapDataFloor1, "stair1", 1400);
                return; // เปลี่ยนชั้นเสร็จให้จบการทำงานปุ่ม F ทันที
            }
        }


            if(player.xDelta <= 40){
                roomManager.changeRoomLeft(camera);
            }

            if(player.xDelta >= roomManager.getWidth()-150){
                roomManager.changeRoomRight(camera);
            }
        }

        //ย้ายมาจาก GamePanel------------------------
        if (gamePanel.getIsTransitioning()) {// แค่เช็คว่าติดสถานะจอดำอยู่มั้ย ถ้าใช่ กดปุ่มไหนก็ทำงานเลย
            gamePanel.startNextDay();
        }
        //จบโค้ดที่ย้ายมาจาก GamePanel------------------------

        //ย้ายมาจาก class Player ----------------------------
        if (e.getKeyCode() == KeyEvent.VK_D) {
            player.checkRight = true;
            player.rightPressed = true; //เปลี่ยนไปเพิ่ม xDelta ใน update ที่จะเรียกใช้ตลอดแทน

            player.moving = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            player.checkRight = false;
            player.leftPressed = true;

            player.moving = true;
        }

        //DIALOGUE นะจ๊ะ
        // อันนี้แค่ลองใส่ไปก่อนนะเดะมาแก้้ ขอไปไล่ดูก่อนว่าอะไรยังไง
        if (e.getKeyCode() == KeyEvent.VK_E) {
            if (gamePanel != null && gamePanel.dialogBox != null && !gamePanel.dialogBox.isVisible()) {
                gamePanel.dialogBox.startDialog(ui.StoryDialog.INTERVIEW);
            }
        }

        //กดSpacebarอ่านต่อ
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            interact();

        }

        //จบโค้ดที่มาจาก Player----------------------------
    }

    @Override
    public void keyReleased(KeyEvent e) {

        //ย้ายมาจาก Player----------------------------
        if (e.getKeyCode() == KeyEvent.VK_D) {
            player.rightPressed = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            player.leftPressed = false;
        }

        player.moving = player.leftPressed || player.rightPressed; //เปลี่ยนค่าทุกตัวแปลให้รุ้ว่าไม่ได้ขยับ
        //จบโค้ดจาก Player------------------------------

    }

    public MouseAdapter getMouseListener() {
    return new MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            System.out.println("คลิกหน้าจอ!");//อันนี้เราใส่ไว้เช็คว่ามันกดได้จริงหรือป่าว ไว้มาลบทีหลัง
            interact();
        }
    };
    }
}