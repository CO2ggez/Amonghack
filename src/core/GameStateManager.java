package core;

import state.*;
import ui.StoryDialog;

public class GameStateManager {
    private int currentDay;
    private AbstractState currentState;
    public GamePanel gamePanel;

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
        int totalScore = gamePanel.getMinigameManager().score; 

        if (totalScore < 8) { 
            gamePanel.gameEnding.startEnding("CG-gameover");
        }else{
            if (bossScore >= 4) {
                gamePanel.gameEnding.startEnding("CG-ending-chief", StoryDialog.ENDING_CHIEF);
            } else if (janitorScore >= 4) {
                gamePanel.gameEnding.startEnding("CG-ending-Janitor", StoryDialog.ENDING_JANITOR);
            } else {
                gamePanel.gameEnding.startEnding("CG-ending-Arrest", StoryDialog.ENDING_CAUGHT);
            }
        }
    }
}