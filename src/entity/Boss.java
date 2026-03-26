package entity;

import util.SpriteSheetLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Boss extends NPC {

    public Boss() {
        super(800,"chiefoffice");

        try {
            BufferedImage idleSheet = ImageIO.read(
                    getClass().getResource("idle/Boss.png")
            );

            if (idleSheet != null) {
                setIdleFrames(
                        SpriteSheetLoader.loadRow(idleSheet, 8, idleSheet.getWidth()/8, 64*6)
                );
            } else {
                System.out.print("no idle sheet");
            }


            BufferedImage walkSheet = ImageIO.read(
                    getClass().getResource("walk/Boss.png")
            );

            if (walkSheet != null) {
                setWalkFrames(
                        SpriteSheetLoader.loadRow(walkSheet, 9, walkSheet.getWidth()/9, 64*6)
                );
            } else {
                System.out.print("no walk sheet");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
