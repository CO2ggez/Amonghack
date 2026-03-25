package network;

import java.util.HashMap;
import java.util.Map;

public class MinigameLocation {
    public static Map<String, int[]> lanLocation = new HashMap<>();

    static {
        lanLocation.put("office", new int[]{74*6, 129*6});
        lanLocation.put("server", new int[]{190*6, 239*6});
        lanLocation.put("art", new int[]{800, 1200});
        lanLocation.put("meeting", new int[]{400, 700});
    }

    public static int[] get(String room) {
        return lanLocation.get(room);
    }
}
