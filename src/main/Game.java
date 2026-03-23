package main;

import core.GamePanel;
import entity.Player;
import java.awt.*;
import javax.swing.*;
import ui.TextBook;

public class Game {
    private Player player = new Player();
    private JFrame frame_main;
    private JButton btn_close;
    private GamePanel panel;
    private TextBook textbook;
    private int screenWidth;
    private int screenHeight;

    public void setPanel(core.GamePanel panel) {
        this.panel = panel;
    }

    public Game() {
        panel = new GamePanel(player);
        panel.setLayout(null);
        player.setPanel(panel);

        screenWidth = panel.getScreenWidth(); //ถ้าเป็นไปได้อยากให้เปลี่ยนขนาดจอที่เดียวละเปลี่ยนหมดเลยอ่ะ
        screenHeight = panel.getScreenHeight();

        frame_main = new JFrame("Amonghack");

        frame_main.setUndecorated(true);
        frame_main.setSize(screenWidth, screenHeight);
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
        btn_close.setBounds(1860, 10, new_width, new_h);
        btn_close.setBorderPainted(false);
        btn_close.setContentAreaFilled(false);
        btn_close.setFocusPainted(false);
        btn_close.setOpaque(false);
        btn_close.addActionListener(e -> System.exit(0));

        player.setBounds(0, 0, screenWidth, screenHeight);
        player.setOpaque(false); //ลบ background ของ Player
        //textbook
        //panel.add(player);
        panel.add(btn_close);
        panel.setComponentZOrder(player, 1); //ดึง player ขึ้นมาข้างหน้าสุด

        frame_main.add(panel, BorderLayout.CENTER);
        frame_main.setLocationRelativeTo(null);
        frame_main.setVisible(true);

        player.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}