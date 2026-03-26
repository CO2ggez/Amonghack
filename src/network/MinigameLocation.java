package network;

import java.util.HashMap;
import java.util.Map;

public class MinigameLocation {
    public static Map<String, int[]> lanLocation = new HashMap<>();

    public static Map<String, int[]> terminalLocation = new HashMap<>();

    static {
        lanLocation.put("office", new int[]{74*6, 129*6});
        lanLocation.put("server", new int[]{190*6, 239*6});
        lanLocation.put("art", new int[]{20*6, 79*6});
        lanLocation.put("meeting", new int[]{102*6, 152*6});

        terminalLocation.put("office", new int[]{173*6, 220*6}); // เปลี่ยนเลขพิกัดตามตำแหน่งคอมในรูปฉาก
        terminalLocation.put("server", new int[]{136*6, 172*6});
        terminalLocation.put("itsupport", new int[]{300*6, 349*6});
        terminalLocation.put("market", new int[]{40*6,87*6});
        terminalLocation.put("art", new int[]{129*6,184*6});
    }

    public static int[] get(String room) {
        return lanLocation.get(room);
    }
    public static int[] getTerminal(String room) {
        return terminalLocation.get(room);
    }
}
