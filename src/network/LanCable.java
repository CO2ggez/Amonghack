package network;

import core.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class LanCable implements Minigame{//underconstruction ครับ
    private JPanel panel;
    private MinigameManager manager;

    public LanCable(MinigameManager manager) {
        this.manager = manager;

    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
