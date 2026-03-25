package network;

import core.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class LanCable implements Minigame{
    private JPanel panel;
    private MinigameManager manager;

    private Image background;
    private Image[] wireImages = new Image[4];

    //hitbox สำหรับคลิก
    private Rectangle[] left = new Rectangle[4];
    private Rectangle[] right = new Rectangle[4];

    private int[] rightOrder = {0,1,2,3};
    private boolean[] connected = new boolean[4];

    private int selected = -1;

    public LanCable(MinigameManager manager) {
        this.manager = manager;

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

        background = new ImageIcon(getClass().getResource("LanImage/LanBG.png")).getImage();
        if  (background == null){
            System.out.println("background is null");
        }

        wireImages[0] = new ImageIcon(getClass().getResource("LanImage/LanYellow.png")).getImage();
        wireImages[1] = new ImageIcon(getClass().getResource("LanImage/LanRed.png")).getImage();
        wireImages[2] = new ImageIcon(getClass().getResource("LanImage/LanBlue.png")).getImage();
        wireImages[3] = new ImageIcon(getClass().getResource("LanImage/LanPink.png")).getImage();

        //loop สร้าง hitbox
        for (int i = 0; i < 4; i++) {
            left[i] = new Rectangle(394, 305 + i * 150,wireImages[i].getWidth(null), wireImages[i].getHeight(null));
            right[i] = new Rectangle(1266, 298 + i * 150, wireImages[i].getWidth(null), wireImages[i].getHeight(null));
        }

        //สุ่มสีด้านขวา
        shuffle();

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e);
            }
        });


    }

    public void draw(Graphics g){
        g.drawImage(background, 0, 0, panel.getWidth(), panel.getHeight(), null);

        //วาดสายซ้าย
        for (int i = 0; i < 4; i++) {
            g.drawImage(
                    wireImages[i],
                    left[i].x+wireImages[i].getWidth(null),
                    left[i].y,
                    -wireImages[i].getWidth(null),
                    wireImages[i].getHeight(null),
                    null
            );
        }

        //วาดสายขวา
        for (int i = 0; i < 4; i++) {
            int colorIndex = rightOrder[i];

            g.drawImage(
                    wireImages[colorIndex],
                    right[i].x,
                    right[i].y,
                    wireImages[colorIndex].getWidth(null),
                    wireImages[colorIndex].getHeight(null),
                    null
            );
        }

        Graphics2D g2 = (Graphics2D) g;

        //วาดเส้นที่เชื่อมแล้ว
        for (int i = 0; i < 4; i++) {
            if (connected[i]) {

                int r = getRightIndex(i);

                int x1 = left[i].x + 170;
                int y1 = left[i].y + 25;
                int x2 = right[r].x + 25;
                int y2 = right[r].y + 25;

                //this is คณิตศาสตร์ เพื่อหามุม และความยาวเส้น
                double angle = Math.atan2(y2 - y1, x2 - x1); //radians
                int length = (int) Math.hypot(x2 - x1, y2 - y1);

                g2.translate(x1, y1); //เปลี่ยยจากที่จะเกิดที่ 0 0 เป็นจุดนี้
                g2.rotate(angle);

                g2.setColor(getColor(i));
                g2.fillRect(0, -10, length, 20);

                //ขยับ origin กลับไปจุดเดิม
                g2.rotate(-angle);
                g2.translate(-x1, -y1);
            }
        }

    }

    private void handleClick(MouseEvent e) {

        for (int i = 0; i < 4; i++) {
            if (left[i].contains(e.getPoint()) && !connected[i]) {
                selected = i;
                panel.repaint();
                return;
            }
        }

        if (selected != -1) {
            for (int i = 0; i < 4; i++) {
                if (right[i].contains(e.getPoint())) {

                    if (rightOrder[i] == selected) {
                        connected[selected] = true;
                    }

                    selected = -1;
                    panel.repaint();
                    checkWin();
                    return;
                }
            }
        }
    }

    private void checkWin() {
        for (boolean b : connected) {
            if (!b) return;
        }
        manager.onWin();
    }

    private int getRightIndex(int colorIndex) {
        for (int i = 0; i < 4; i++) {
            if (rightOrder[i] == colorIndex) return i;
        }
        return -1;
    }

    private void shuffle() {
        java.util.List<Integer> list = Arrays.asList(0,1,2,3);
        Collections.shuffle(list);
        for (int i = 0; i < 4; i++) {
            rightOrder[i] = list.get(i);
        }
    }

    private Color getColor(int i) {
        return switch (i) {
            case 2 -> Color.BLUE;
            case 3 -> Color.MAGENTA;
            case 1 -> Color.RED;
            case 0 -> Color.YELLOW;
            default -> Color.WHITE;
        };
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
