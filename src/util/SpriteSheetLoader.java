package util;

import java.awt.image.BufferedImage;

public class SpriteSheetLoader {

    //แยกรูปจาก sprite sheet ออกเป็น frameๆ
    //return เป็น array ของรูป
    public static BufferedImage[] loadRow(
            BufferedImage sheet,
            int frameCount,
            int frameWidth,
            int frameHeight
    ) {
        BufferedImage[] frames = new BufferedImage[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = sheet.getSubimage(
                    i * frameWidth,
                    0,
                    frameWidth,
                    frameHeight
            );
        }

        return frames;
    }

}
