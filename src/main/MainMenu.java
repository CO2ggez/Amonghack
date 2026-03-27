package main;

import java.awt.*;
import javax.swing.*;

public class MainMenu extends JPanel{
    private JButton btn_start;
    private Image CGMainmenu;
    private ImageIcon logo;
    private JButton btn_close;

    public JButton getBtn_start(){return btn_start;}


    public MainMenu(Game g){
        ImageIcon startIcon = new ImageIcon(getClass().getResource("start.png"));
        btn_start = new JButton(startIcon);
        CGMainmenu = new ImageIcon(getClass().getResource("CG_inprogress.png")).getImage();
//        logo = new ImageIcon(getClass().getResource("close.png")).getImage();

        ImageIcon originalIcon = new ImageIcon(getClass().getResource("close.png"));
        int width  = originalIcon.getIconWidth();
        int h = originalIcon.getIconHeight();
        int new_width  = 50;
        int new_h = (h * new_width) / width;
        Image scaledImage = originalIcon.getImage().getScaledInstance(new_width, new_h, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        btn_close = new JButton(scaledIcon);
        btn_close.setBounds(1860, 1020, new_width, new_h);
        btn_close.setBorderPainted(false);
        btn_close.setContentAreaFilled(false);
        btn_close.setFocusPainted(false);
        btn_close.setOpaque(false);
        btn_close.addActionListener(e ->{
            System.exit(0);
        });
        add(btn_close);


        setLayout(null);
        btn_start.setBounds(1320, 540, 500, 160);
        add(btn_start);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดรูปเต็มจอ
        g.drawImage(CGMainmenu, 0, 0, getWidth(), getHeight(), this);
    }

}
