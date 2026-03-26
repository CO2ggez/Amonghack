package entity;

import util.SpriteSheetLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Cat extends NPC {

    public Cat() {
        super(64*6,"chiefoffice");
        y = 64*6 + 125;

        try {
            BufferedImage idleSheet = ImageIO.read(
                    getClass().getResource("idle/cat.png")
            );

            if (idleSheet != null) {
                setIdleFrames(
                        SpriteSheetLoader.loadRow(idleSheet, 9, idleSheet.getWidth()/9, idleSheet.getHeight())
                );
                setWalkFrames(
                        SpriteSheetLoader.loadRow(idleSheet, 9, idleSheet.getWidth()/9, idleSheet.getHeight())
                );
                width = idleSheet.getWidth()/9;
                height = idleSheet.getHeight();
            } else {
                System.out.print("no idle sheet");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
