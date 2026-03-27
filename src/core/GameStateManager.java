package core;

import state.*;
import ui.StoryDialog;

public class GameStateManager {
    private int currentDay;
    private AbstractState currentState;
    public GamePanel gamePanel;

    // --- เพิ่มตัวแปร 3 ตัวนี้ตรงนี้ครับ (แก้ Error ตัวแดง) ---
    private int currentEndingStep = 0;
    private boolean passBoss = false;
    private boolean passJanitor = false;
    // ------------------------------------------------

    public GameStateManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        currentDay = 1;
        loadState(currentDay);
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void nextDay() {
        if (currentDay < 5) {
            currentDay++;
            loadState(currentDay);
        }
    }

    private void loadState(int day) {
        switch (day) {
            case 1: currentState = new Day1State(gamePanel); break;
            case 2: currentState = new Day2State(gamePanel); break;
            case 3: currentState = new Day3State(gamePanel); break;
            case 4: currentState = new Day4State(gamePanel); break;
            case 5: currentState = new Day5State(gamePanel); break;
        }
    }

    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }

    public void draw(java.awt.Graphics2D g) {
        if (currentState != null) {
            currentState.draw(g);
        }
    }

    public void checkEndGame() {
        gamePanel.showingEnding = true;

        int bossScore = gamePanel.getMinigameManager().helpBossScore;
        int janitorScore = gamePanel.getMinigameManager().helpJanitorScore;
        //int totalScore = gamePanel.getMinigameManager().score;

        /*if (totalScore < 8) {
            // ถ้าคะแนนไม่ถึง 8 ให้ Game Over ทันที
            gamePanel.gameEnding.startEnding("CG-gameover");
            currentEndingStep = 99; // กำหนดไว้เพื่อให้รู้ว่าไม่มีคิวต่อแล้ว
        } else {
         */   // ถ้าคะแนนถึง 8 เซ็ตค่าว่าผ่านเงื่อนไขใครบ้าง
            passBoss = (bossScore >= 4);
            passJanitor = (janitorScore >= 4);

            // เริ่มเล่นฉากจบคิวที่ 1
            currentEndingStep = 1;
            playNextEnding();
       // }
    }

    // --- เมธอดใหม่สำหรับเช็คและเล่นฉากจบถัดไป ---
    public void playNextEnding() {
        if (currentEndingStep == 1) {
            // คิวที่ 1: ขึ้น Ending ธรรมดา "เสมอ" ในทุกกรณีที่สอบผ่าน
            currentEndingStep++;
            gamePanel.gameEnding.startEnding("CG-ending-Arrest", StoryDialog.ENDING_CAUGHT);
        }
        else if (currentEndingStep == 2) {
            // คิวที่ 2: เช็ค Ending หัวหน้า
            currentEndingStep++;
            if (passBoss) {
                gamePanel.gameEnding.startEnding("CG-ending-chief", StoryDialog.ENDING_CHIEF);
            } else {
                playNextEnding(); // ถ้าคะแนนไม่ถึง ให้ข้ามคิวนี้ไปทันที
            }
        }
        else if (currentEndingStep == 3) {
            // คิวที่ 3: เช็ค Ending ภารโรง
            currentEndingStep++;
            if (passJanitor) {
                gamePanel.gameEnding.startEnding("CG-ending-Janitor", StoryDialog.ENDING_JANITOR);
            } else {
                playNextEnding(); // ถ้าคะแนนไม่ถึง ให้ข้ามคิวนี้ไปทันที
            }
        }
        else {
            // ถ้าเล่นครบทุกคิวแล้ว (หรือเป็น Game Over) ให้จบเกม
            System.exit(0);
        }
    }
}