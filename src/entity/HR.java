package entity;

import util.SpriteSheetLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class HR extends NPC {

    public HR() {
        super(1200,"lift1");

        try {
            BufferedImage idleSheet = ImageIO.read(
                    getClass().getResource("idle/HR.png")
            );

            if (idleSheet != null) {
                setIdleFrames(
                        SpriteSheetLoader.loadRow(idleSheet, 9, 64*6, 64*6)
                );
            } else {
                System.out.print("no idle sheet");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
