package main;

import core.GamePanel;
import entity.Player;
import ui.TextBook;

import java.awt.*;
import javax.swing.*;

public class Game {
    private Player player = new Player();
    private JFrame frame_main;
    private JButton btn_close;
    private GamePanel panel;
    private TextBook textbook;

    public Game() {
        panel = new GamePanel(player);
        panel.setLayout(null);

        frame_main = new JFrame("Amonghack");
        frame_main.setUndecorated(true);
        frame_main.setSize(1720, 800);
        frame_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_main.setLayout(new BorderLayout());

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("close.png"));
        int width  = originalIcon.getIconWidth();
        int h = originalIcon.getIconHeight();
        int new_width  = 50;
        int new_h = (h * new_width) / width;
        Image scaledImage = originalIcon.getImage().getScaledInstance(new_width, new_h, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        btn_close = new JButton(scaledIcon);
        btn_close.setBounds(1660, 10, new_width, new_h);
        btn_close.setBorderPainted(false);
        btn_close.setContentAreaFilled(false);
        btn_close.setFocusPainted(false);
        btn_close.setOpaque(false);
        btn_close.addActionListener(e -> System.exit(0));

        player.setBounds(0, 0, 1720, 800);
        player.setOpaque(false);           //ลบ background ของ Player
        //textbook

        
        panel.add(player);
        panel.add(btn_close);
        panel.setComponentZOrder(player, 0); //ดึง player ขึ้นมาข้างหน้าสุด

        frame_main.add(panel, BorderLayout.CENTER);
        frame_main.setLocationRelativeTo(null);
        frame_main.setVisible(true);

        player.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}