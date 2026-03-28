package ui;

import core.TimeManager;
import core.GameStateManager;
import util.FontUtil;

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
        g.setFont(FontUtil.THAI.deriveFont(100f));
        g.drawString(tm.getTimeString(), 40, 70);

        //จบวัน
        if (tm.isDayEnded()) {

            g.setColor(new Color(0, 0, 0, 255));
            g.fillRect(0, 0, 1920, 1080);

            g.setColor(Color.WHITE);
            g.setFont(FontUtil.THAI.deriveFont(Font.BOLD, 150f));

            String text;
            String promptText;

            // เช็คว่าเป็นวันที่ 5 (หรือมากกว่า) หรือไม่
            if (gsm.getCurrentDay() >= 5) {
                text = "The End";
                promptText = "Click the [X] button on bottom right to Exit";
            } else {
                text = "Day " + gsm.getCurrentDay() + " Ended";
                promptText = "Press ANY KEY to start Next Day";
            }

            FontMetrics metrics = g.getFontMetrics();
            int x = (1920 - metrics.stringWidth(text)) / 2;
            int y = ((1080 - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(text, x, y);

            g.setFont(FontUtil.THAI.deriveFont(Font.PLAIN, 80f));
            int promptX = (1920 - g.getFontMetrics().stringWidth(promptText)) / 2;
            g.drawString(promptText, promptX, y + 60);
        }
    }
}