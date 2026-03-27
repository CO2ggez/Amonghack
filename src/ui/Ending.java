package ui;

import java.awt.Graphics;

public class Ending {
    private CgLoader cgLoader;
    private String[] storyLines;     
    private int currentLineIndex;    
    private boolean isFinished;      
    private String currentCgName;    

    public Ending(CgLoader cgLoader) {
        this.cgLoader = cgLoader;
        this.isFinished = true; 
    }

    // --- ADDED: เมธอดใหม่ สำหรับเรียกโชว์แค่รูปภาพอย่างเดียว (ไม่มีกล่องข้อความ) ---
    public void startEnding(String cgName) {
        this.currentCgName = cgName;
        this.storyLines = null; // เซ็ตให้ไม่มีข้อความ
        this.currentLineIndex = 0;
        this.isFinished = false;
        
        // สั่ง CgLoader ให้แสดงแค่ภาพ
        cgLoader.setCg(cgName);
    }

    // --- เมธอดเดิม: สำหรับภาพพร้อมข้อความ ---
    public void startEnding(String cgName, String[] lines) {
        this.currentCgName = cgName;
        this.storyLines = lines;
        this.currentLineIndex = 0;
        this.isFinished = false;
        updateCgView();
    }

    public void nextLine() {
        if (isFinished) return; 

        // --- ADDED: ถ้าเป็นโหมดแสดงแค่ภาพ (ไม่มีข้อความ) พอกด Spacebar/คลิก ก็จะถือว่าจบเลย ---
        if (storyLines == null) {
            isFinished = true;
            cgLoader.hide();
            return;
        }

        currentLineIndex++; 
        
        if (currentLineIndex >= storyLines.length) {
            isFinished = true; 
            cgLoader.hide(); 
        } else {
            updateCgView(); 
        }
    }

    private void updateCgView() {
        if (storyLines != null && currentLineIndex < storyLines.length) {
            cgLoader.setCgWithStory(currentCgName, storyLines[currentLineIndex]);
        }
    }

    public void draw(Graphics g) {
        if (!isFinished) {
            cgLoader.draw(g);
        }
    }

    public boolean isFinished() {
        return isFinished;
    }
}