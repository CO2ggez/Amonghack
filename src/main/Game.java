package main;

import core.GamePanel;
import entity.Player;
import java.awt.*;
import javax.swing.*;
import ui.TextBook;
import audio.Sound;

public class Game {
    private Player player = new Player();
    private JFrame frame_main;
    private JButton btn_close;
    private GamePanel panel;
    private TextBook textbook;
    private final int screenWidth = 1920;
    private final int screenHeight = 1080;
    private JPanel cardcontain;
    private CardLayout cardLayout;
    private MainMenu mainMenu;
    private Sound sound;

    public void setPanel(core.GamePanel panel) {
        this.panel = panel;
    }

    public Game() {
        frame_main = new JFrame("Amonghack");
        cardLayout = new CardLayout();
        cardcontain = new JPanel(cardLayout);
        mainMenu = new MainMenu(this);
        sound = new Sound();
        sound.loopSound("menu_bg");

        cardcontain.add(mainMenu, "mainmenu");

        mainMenu.getBtn_start().addActionListener(e -> {

            sound.playSound("click1");
            sound.stopSound("menu_bg");
            startgame();
            panel.requestFocusInWindow();}
        );

        frame_main.setUndecorated(true);
        frame_main.setSize(screenWidth, screenHeight);
        frame_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_main.setLayout(new BorderLayout());

        frame_main.add(cardcontain, BorderLayout.CENTER);
        frame_main.setLocationRelativeTo(null);
        frame_main.setVisible(true);

    }

    public void startgame(){
        player = new Player();
        panel = new GamePanel(player);
        panel.setLayout(null);
        player.setPanel(panel);
        cardcontain.add(panel, "gamepanel");

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("close.png"));
        int width  = originalIcon.getIconWidth();
        int h = originalIcon.getIconHeight();
        int new_width  = 50;
        int new_h = (h * new_width) / width;
        Image scaledImage = originalIcon.getImage().getScaledInstance(new_width, new_h, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        System.out.println("Let's go");

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
        panel.add(player);
        panel.setComponentZOrder(player, 1); //ดึง player ขึ้นมาข้างหน้าสุด
        panel.add(btn_close);

        cardcontain.revalidate();
        cardcontain.repaint();

        cardLayout.show(cardcontain, "gamepanel");

        player.requestFocusInWindow();
    }


    public static void main(String[] args) {
        //แก้จอยืดครับ
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(Game::new);
    }
}