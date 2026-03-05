package ui;

import core.TimeManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class TimeUI {
    private TimeManager tm;

    public TimeUI(TimeManager tm) {
        this.tm = tm;
    }

    public void draw(Graphics2D g) {
        //เวลามุมซ้ายบน
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(tm.getTimeString(), 40, 50);

        //จบวัน
        if (tm.isDayEnded()) {
            g.setColor(Color.RED);
            Font font = new Font("Arial", Font.BOLD, 80);
            g.setFont(font);

            String text = "DAY END";

            //สูตรคำนวณกึ่งกลางหน้าจอ
            java.awt.FontMetrics metrics = g.getFontMetrics(font);
            int x = (1720 - metrics.stringWidth(text)) / 2;
            int y = ((800 - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(text, x, y);
        }
    }
}