package core;

import state.*;

public class GameStateManager {
    private int currentDay;
    private AbstractState currentState;

    public GameStateManager() {
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

    //method นี้จะสลับคลาสตามเลขวัน
    private void loadState(int day) {
        switch (day) {
            case 1: currentState = new Day1State(); break;
            case 2: currentState = new Day2State(); break;
            case 3: currentState = new Day3State(); break;
            case 4: currentState = new Day4State(); break;
            case 5: currentState = new Day5State(); break;
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
}