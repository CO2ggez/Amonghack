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

import core.TimeManager;

public class Game {
    public Game() {
        // ทดสอบระบบ Logic เวลาเพียวๆ
        TimeManager tm = new TimeManager();
        tm.start();
    }

    public static void main(String[] args) {
        new Game();
    }
}