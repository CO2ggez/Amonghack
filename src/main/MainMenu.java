package main;

import java.awt.*;
import javax.swing.*;

public class MainMenu extends JPanel{
    private JButton btn_start;
    private Image CGMainmenu;
    private JButton btn_close;

    public JButton getBtn_start(){return btn_start;}


    public MainMenu(Game g){
        ImageIcon startIcon = new ImageIcon(getClass().getResource("start.png"));
        ImageIcon quitIcon = new ImageIcon(getClass().getResource("quit.png"));
        btn_start = new JButton(startIcon);
        btn_close = new JButton(quitIcon);
        CGMainmenu = new ImageIcon(getClass().getResource("CG-start-Mainmenu.png")).getImage();

        btn_close.addActionListener(e ->{
            System.exit(0);
        });



        setLayout(null);
        btn_start.setBounds(216, 618, 372, 108);
        btn_close.setBounds(216, 756,372,108);
        add(btn_start);
        add(btn_close);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดรูปเต็มจอ
        g.drawImage(CGMainmenu, 0, 0, getWidth(), getHeight(), this);
    }

}
