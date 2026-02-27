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
        // วาดตัวเลขเวลามุมซ้ายบน
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(tm.getTimeString(), 40, 50);

        // ถ้า TimeManager แสดงจบวัน ให้วาดตัวหนังสือสีแดงกลางจอ
        if (tm.isDayEnded()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            g.drawString("DAY END", 230, 330);
        }
    }
}