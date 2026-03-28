package core;

public class TimeManager implements Runnable {
    private int inGameMinutes = 0; // 0 คือเริ่มที่ 00:00 AM
    private boolean isPaused = false;
    private boolean isDayEnded = false; // เช็คว่าหมดวันยัง
    private boolean running = false;
    private Thread timeThread;

    //เริ่มนับเวลา
    public void start() {
        if (timeThread == null || !running) {
            running = true;
            timeThread = new Thread(this, "GameTimeThread");
            timeThread.start();
        }
    }

    //คำสั่งเอาไว้หยุดเวลาชั่วคราว
    public void setPaused(boolean p) {
        isPaused = p;
    }

    public boolean isDayEnded() {
        return isDayEnded;
    }

    public void forceEndDay() {
        isDayEnded = true;
        isPaused = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1350); //ค่าปกติ 2000 เทสใช้ 5 ให้ time มันวิ่งไว
                if (!isPaused && !isDayEnded) {
                    inGameMinutes++;
                    if (inGameMinutes >= 360) {
                        isDayEnded = true;
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
    }

    public double getHours() {
        return (double) inGameMinutes /60;
    }
}