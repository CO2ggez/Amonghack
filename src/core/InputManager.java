package core;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ui.Camera;
import map.RoomManager;
import entity.Player;

public class InputManager implements KeyListener {
    private Camera camera;
    private RoomManager roomManager;
    private GamePanel gamePanel;
    private Player player;

    public void interact() {
        gamePanel.dialogBox.nextText();
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