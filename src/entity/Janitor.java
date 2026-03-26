package entity;

import util.SpriteSheetLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Janitor extends NPC {

    public Janitor() {
        super(300,"liftG");

        try {
            BufferedImage idleSheet = ImageIO.read(
                    getClass().getResource("idle/Janitor.png")
            );

            if (idleSheet != null) {
                setIdleFrames(
                        SpriteSheetLoader.loadRow(idleSheet, 6, 64*6, 64*6)
                );
            } else {
                System.out.print("no idle sheet");
            }


            BufferedImage walkSheet = ImageIO.read(
                    getClass().getResource("walk/Janitor.png")
            );

            if (walkSheet != null) {
                setWalkFrames(
                        SpriteSheetLoader.loadRow(walkSheet, 8, 64*6, 64*6)
                );
            } else {
                System.out.print("no walk sheet");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
