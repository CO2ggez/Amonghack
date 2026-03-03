package core;
public class GameLoop implements Runnable{

    private final double FPS = 60;
    private boolean running = false; //เอาไว้คุมการทำงานloop
    private Thread gameThread;

    private final GamePanel panel;

    private double frame_game = 0;
    private long lastTime;
    private long curTime;

    public GameLoop(GamePanel panel){
        this.panel = panel;
        panel.update();
        panel.repaint();
    }

    @Override
    public void run(){
        double drawFrame = 1000000000.0 / FPS;
        lastTime = System.nanoTime();
        while (running) { 
            curTime = System.nanoTime();
            frame_game += (curTime - lastTime) / drawFrame;
            lastTime = curTime;

            if (frame_game >= 1) {
                panel.update();
                panel.repaint();
                frame_game--;
            }
        }
    }


    public void start(){
        if (gameThread == null) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stop(){
        running = false;
    }

}