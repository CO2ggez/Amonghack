package event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private List<TriggerZone> zones;
    private boolean showImage;
    private String activeZoneName;

    public EventManager() {
        zones = new ArrayList<>();
        showImage = false;
        activeZoneName = "";
    }

    public void addZone(TriggerZone zone) {
        zones.add(zone);
    }

    public void checkClick(int mouseX, int mouseY) {
        boolean clickedOnZone = false;

        for (TriggerZone zone : zones) {
            if (zone.isHit(mouseX, mouseY)) {
                if (showImage && activeZoneName.equals(zone.getName())) {
                    showImage = false;
                    activeZoneName = "";
                } else {
                    showImage = true;
                    activeZoneName = zone.getName();
                }
                clickedOnZone = true;
                break;
            }
        }

        if (!clickedOnZone) {
            showImage = false;
            activeZoneName = "";
        }
    }

    public boolean isShowImage() {
        return showImage;
    }

    public String getActiveZoneName() {
        return activeZoneName;
    }

    // เพิ่มเมธอดนี้ไว้ด้านล่างสุดในคลาส EventManager
    public void closeEvent() {
        this.showImage = false;
        this.activeZoneName = "";
    }
}