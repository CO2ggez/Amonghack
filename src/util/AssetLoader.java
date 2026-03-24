package util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AssetLoader {
    // เมธอดสำหรับโหลดรูปภาพ
    public static BufferedImage loadImage(String path) {
        BufferedImage img = null;
        try {
            // โหลดรูปภาพจากโฟลเดอร์ src (ต้องมีรูปอยู่ในพาธที่ระบุ)
            img = ImageIO.read(AssetLoader.class.getResourceAsStream(path));
        } catch (IOException | NullPointerException e) {
            System.err.println("ไม่พบไฟล์รูปภาพที่: " + path);
            e.printStackTrace();
        }
        return img;
    }
}