package state;

import java.awt.*;

public abstract class AbstractState {
    public abstract void update();
    public abstract void draw(Graphics2D g);
}
