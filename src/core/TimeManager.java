package core;

public class TimeManager implements Runnable {
    private int inGameMinutes = 0; // 0 คือเริ่มที่ 00:00 AM
    private boolean isPaused = false; // เอาไว้หยุดเวลาตอนคุย ทำไว้ก่อน
    private boolean isDayEnded = false; // เช็คว่าหมดวันยัง
    private boolean running = false;
    private Thread timeThread;

    //เริ่มนับเวลา
    public void start() {
        if (timeThread == null || !running) {
            running = true;
            timeThread = new Thread(this, "GameTimeThread");
            timeThread.start();
            System.out.println("Start");
        }
    }

    public void stop() {
        running = false;
    }

    //คำสั่งเอาไว้หยุดเวลาชั่วคราว เอาไว้หยุดเวลาตอนคุย ทำรอ dialog
    public void setPaused(boolean p) {
        isPaused = p;
        if(p) System.out.println("เวลาหยุดเดิน");
        else System.out.println("เวลาเดินต่อ");
    }

    public boolean isDayEnded() {
        return isDayEnded;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(2000); //ค่าปกติ 2000 เทสใช้ 10 ให้มัน time มันวิ่งไว
                if (!isPaused && !isDayEnded) {
                    inGameMinutes++;
                    System.out.println(getTimeString());
                    if (inGameMinutes >= 360) {
                        isDayEnded = true;
                        System.out.println("End of the day");
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

    //ใช้ตอนจะขึ้นวันใหม่
    public void resetDay() {
        this.inGameMinutes = 0; // กลับไปเริ่มที่ 00:00 AM
        this.isDayEnded = false; // เอาป้ายจบวันออก
        System.out.println("Time reset.");
    }
}