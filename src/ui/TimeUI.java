package ui;

import core.TimeManager;
import core.GameStateManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.FontMetrics;

public class TimeUI {
    private TimeManager tm;
    private GameStateManager gsm;

    public TimeUI(TimeManager tm, GameStateManager gsm) {
        this.tm = tm;
        this.gsm = gsm;
    }

    public void draw(Graphics2D g) {
        //เวลามุมซ้ายบน
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(tm.getTimeString(), 40, 50);

        //จบวัน
        if (tm.isDayEnded()) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, 1720, 800);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Tahoma", Font.BOLD, 80));
            String text = "Day " + gsm.getCurrentDay() + " Ended";

            FontMetrics metrics = g.getFontMetrics();
            int x = (1720 - metrics.stringWidth(text)) / 2;
            int y = ((800 - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(text, x, y);

            g.setFont(new Font("Tahoma", Font.PLAIN, 30));
            String promptText = "Press ANY KEY to start Next Day";
            int promptX = (1720 - g.getFontMetrics(new Font("Tahoma", Font.PLAIN, 30)).stringWidth(promptText)) / 2;
            g.drawString(promptText, promptX, y + 60);
        }
    }
}