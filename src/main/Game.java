//package main;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class Game {
//
//    JFrame frame_main;
//    JButton btn_close;
//
//    public Game() {
//
//        frame_main = new JFrame("Amonghack");
//        frame_main.setUndecorated(true);
//        frame_main.setSize(380, 120);
//        frame_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame_main.setLayout(null);
//
//        ImageIcon originalIcon = new ImageIcon(getClass().getResource("close.png"));
//
//        int originalWidth = originalIcon.getIconWidth();
//        int originalHeight = originalIcon.getIconHeight();
//
//        int newWidth = 50;
//        int newHeight = (originalHeight * newWidth) / originalWidth;
//
//        Image scaledImage = originalIcon.getImage()
//                .getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
//
//        ImageIcon scaledIcon = new ImageIcon(scaledImage);
//
//        btn_close = new JButton(scaledIcon);
//
//        btn_close.setBounds(frame_main.getWidth() - newWidth - 10, 10, newWidth, newHeight);
//
//        btn_close.setBorderPainted(false);
//        btn_close.setContentAreaFilled(false);
//        btn_close.setFocusPainted(false);
//        btn_close.setOpaque(false);
//
//        btn_close.addActionListener(e -> System.exit(0));
//
//        frame_main.add(btn_close);
//
//        frame_main.setLocationRelativeTo(null);
//        frame_main.setVisible(true);
//    }
//
//    public static void main(String[] args) {
//        new Game();
//    }
//}

package main;

import core.GamePanel;
import javax.swing.*;
import java.awt.*;

public class Game {
    public Game() {
        JFrame frame = new JFrame("Amonghack - Test Time UI");
        frame.setUndecorated(true);
        frame.setSize(800, 600); // ขนาดจอ 800x600
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon(getClass().getResource("close.png"));
        Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JButton btn_close = new JButton(new ImageIcon(scaled));
        btn_close.setBounds(740, 10, 50, 50); // วางไว้มุมขวาบน
        btn_close.setBorderPainted(false);
        btn_close.setContentAreaFilled(false);
        btn_close.setFocusPainted(false);
        btn_close.addActionListener(e -> System.exit(0));

        // สร้างกระดานเกม
        GamePanel gamePanel = new GamePanel();
        gamePanel.setLayout(null);

        gamePanel.add(btn_close);

        frame.add(gamePanel);

        // เริ่มเกม
        gamePanel.startGameThread();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Game();
    }
}