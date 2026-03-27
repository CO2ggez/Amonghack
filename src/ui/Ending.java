package ui;

import java.awt.Graphics;

public class Ending {
    // --- ตัวแปรสำหรับควบคุมฉากจบ ---
    private CgLoader cgLoader;
    private String[] storyLines;     // เก็บข้อความเนื้อเรื่องเป็นหน้าๆ
    private int currentLineIndex;    // ตัวนับว่าตอนนี้กำลังแสดงข้อความหน้าไหนอยู่
    private boolean isFinished;      // สถานะเช็คว่าฉากจบนี้เล่นจบหรือยัง
    private String currentCgName;    // ชื่อไฟล์ภาพ CG ที่กำลังใช้อยู่

    // Constructor รับค่า CgLoader จากระบบหลักมาใช้
    public Ending(CgLoader cgLoader) {
        this.cgLoader = cgLoader;
        this.isFinished = true; // เริ่มต้นมาให้สถานะคือยังไม่ได้เล่น
    }

    // --- เมธอดสำหรับ "เริ่ม" เล่นฉากจบ ---
    public void startEnding(String cgName, String[] lines) {
        this.currentCgName = cgName;
        this.storyLines = lines;
        this.currentLineIndex = 0;
        this.isFinished = false;

        updateCgView();
    }

    // --- เมธอดสำหรับ "เปลี่ยนหน้า" เนื้อเรื่อง ---
    public void nextLine() {
        if (isFinished) return;

        currentLineIndex++;
        
        if (currentLineIndex >= storyLines.length) {
            isFinished = true; 
            cgLoader.hide();
        } else {
            updateCgView(); 
        }
    }

    // เมธอดส่วนตัวสำหรับส่งข้อมูลไปให้ CgLoader วาด
    private void updateCgView() {
        if (storyLines != null && currentLineIndex < storyLines.length) {
            cgLoader.setCgWithStory(currentCgName, storyLines[currentLineIndex]);
        }
    }

    // --- เมธอดสำหรับวาดลงจอ ---
    public void draw(Graphics g) {
        if (!isFinished) {
            cgLoader.draw(g);
        }
    }

    // --- เมธอดสำหรับเช็คว่าฉากจบเล่นจบสมบูรณ์หรือยัง ---
    public boolean isFinished() {
        return isFinished;
    }

    
}