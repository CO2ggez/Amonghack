package core;

public class TimeManager implements Runnable {
    private int inGameMinutes = 0; // 0 คือเริ่มที่ 00:00 AM
    private boolean isPaused = false; // เอาไว้หยุดเวลาตอนคุย
    private boolean isDayEnded = false; // เช็คว่าหมดวันยัง
    private boolean running = false;
    private Thread timeThread;

    //เริ่มนับเวลา
    public void start() {
        if (timeThread == null || !running) {
            running = true;
            timeThread = new Thread(this, "GameTimeThread");
            timeThread.start();
            System.out.println("ระบบ: เริ่มจับเวลา...");
        }
    }

    public void stop() {
        running = false;
    }

    // คำสั่งเอาไว้หยุดเวลาชั่วคราว (ตอนเปิดหน้าต่างคุย)
    public void setPaused(boolean p) {
        isPaused = p;
        if(p) System.out.println("ระบบ: เวลาหยุดเดิน...");
        else System.out.println("ระบบ: เวลาเดินต่อ...");
    }

    public boolean isDayEnded() {
        return isDayEnded;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(10); //ค่าปกติ 2000 เทสเลยใช้ 10

                if (!isPaused && !isDayEnded) {
                    inGameMinutes++;
                    System.out.println("เวลาในเกม: " + getTimeString());
                    if (inGameMinutes >= 360) {
                        isDayEnded = true;
                        System.out.println("!!! จบวัน !!!");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTimeString() {
        int h = inGameMinutes / 60;
        int m = inGameMinutes % 60;
        return String.format("%02d:%02d AM", h, m);
    }
}