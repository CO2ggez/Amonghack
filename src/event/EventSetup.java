package event;

public class EventSetup {
    private EventManager eventManager;

    public EventSetup(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void loadZones() {
        // กำหนดชื่อโซน, พิกัด X (ในฉาก), พิกัด Y, ความกว้าง, ความสูง
        // ตัวเลข 600, 200 คือพิกัดสมมติของลิฟต์ในฉาก คุณต้องปรับให้ตรงกับรูปภาพลิฟต์ของคุณ
        eventManager.addZone(new TriggerZone("Elevator_Panel", 898, 391, 200, 200));
    }
}