package entity;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
public class Player extends JPanel{
    private int xDelta = 0;
    private int yDelta = 0;
    public Player(){
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.fillRect(100+xDelta,100,200,50);

    }

}
