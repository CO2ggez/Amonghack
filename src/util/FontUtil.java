package util;

import java.awt.Font;

public class FontUtil {
    public static Font THAI = load();

    private static Font load() {
        try {
            return Font.createFont(
                    Font.TRUETYPE_FONT,
                    FontUtil.class.getResourceAsStream("ZF#2ndPixelus.ttf")
            ).deriveFont(36f);
        } catch (Exception e) {
            //ถ้าหาfontไม่เจอใช้อันนี้
            return new Font("Tahoma", Font.BOLD, 16);
        }
    }
}