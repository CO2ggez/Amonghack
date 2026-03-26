package entity;

import util.SpriteSheetLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ITSupport extends NPC {

    public ITSupport() {
        super(500,"itsupport");

        try {
            BufferedImage idleSheet = ImageIO.read(
                    getClass().getResource("idle/ITSupport.png")
            );

            if (idleSheet != null) {
                setIdleFrames(
                        SpriteSheetLoader.loadRow(idleSheet, 9, 64*6, 64*6)
                );
            } else {
                System.out.print("no idle sheet");
            }


            BufferedImage walkSheet = ImageIO.read(
                    getClass().getResource("walk/ITSupport.png")
            );

            if (walkSheet != null) {
                setWalkFrames(
                        SpriteSheetLoader.loadRow(walkSheet, 7, 64*6, 64*6)
                );
            } else {
                System.out.print("no walk sheet");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
