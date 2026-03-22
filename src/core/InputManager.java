package core;

import ui.TextBook;

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

    public InputManager(Camera camera, RoomManager roomManager,GamePanel panel,Player player) {

        this.gamePanel = panel;
        this.camera = camera;
        this.roomManager = roomManager;
        this.player = player;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //ระบบการกด F เปลี่ยนห้องเมื่ออยู่ขอบแมพ ไม่ซ้ายก้ขวา
        if(e.getKeyCode() == KeyEvent.VK_F){

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
        if (e.getKeyCode() == KeyEvent.VK_E) {
            if (!player.panel.dialogBox.isVisible()) {
                String[] bossText = {
                        "สวัสดีพนักงานใหม่...",
                        "งานของนายวันนี้คือไปซ่อมเซิร์ฟเวอร์ซะ",
                        "ลาออกไปซะ"
                };
                player.panel.dialogBox.startDialog(bossText);
                player.panel.timeManager.setPaused(true);

            }
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
}
