package audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.FloatControl;

public class Sound {
    private Map<String, Clip> clips = new HashMap<>();

    // ใช้ ExecutorService (Runnable) เพื่อให้โหลดและเล่นเสียงแยก Thread เกมจะได้ไม่ค้าง
    private final ExecutorService soundWorker = Executors.newSingleThreadExecutor();

    public Sound() {

        loadSound("walk", "/audio/SoundWAV/walk.wav");
        loadSound("walk2", "/audio/SoundWAV/walk2.wav");
        loadSound("door", "/audio/SoundWAV/door.wav");
        loadSound("bg1", "/audio/SoundWAV/bg1.wav");
        loadSound("ringLift", "/audio/SoundWAV/ringLift.wav");
    }

    private void loadSound(String name, String path) {
        soundWorker.execute(() -> {
            try {
                // ดึงไฟล์จาก Resource ภายในโปรเจกต์
                InputStream is = getClass().getResourceAsStream(path);

                if (is == null) {
                    System.err.println("ไม่พบไฟล์เสียงที่ Path: " + path);
                    return;
                }

                // แปลงไฟล์เป็น Stream ที่ Java Sound เข้าใจ
                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);

                Clip clip = AudioSystem.getClip();
                clip.open(ais);

                // เก็บลง HashMap
                clips.put(name, clip);
                System.out.println("โหลดสำเร็จ: " + name);

            } catch (Exception e) {
                System.err.println("เกิดข้อผิดพลาดในการโหลดเสียง: " + name);
                e.printStackTrace();
            }
        });
    }

    // เมธอดสำหรับสั่งเล่นเสียง
    public void playSound(String name) {
        soundWorker.execute(() -> {
            Clip clip = clips.get(name);
            if (clip != null) {
                clip.setFramePosition(0); // รีเซ็ตไปเริ่มต้นใหม่ทุกครั้ง
                clip.start();
            }
        });
    }

    // เมธอดหยุดเสียง
    public void stopSound(String name) {
        Clip clip = clips.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    // ลูปเพลง
    public void loopSound(String name) {
        soundWorker.execute(() -> { // ให้รันใน Worker เพื่อรอโหลดให้เสร็จก่อน
            Clip clip = clips.get(name);
            if (clip != null) {
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                System.out.println("เริ่มเล่นเพลงวนลูป: " + name);
            } else {
                System.err.println("ไม่สามารถเล่นเพลงได้เพราะ clip เป็น null: " + name);
            }
        });
    }

    public void setVolume(String name, float volume) {
        soundWorker.execute(() -> { // เข้าคิวรอโหลดเสร็จ
            if (clips.containsKey(name)) {
                Clip clip = clips.get(name);
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);

                    if (dB < gainControl.getMinimum()) dB = gainControl.getMinimum();
                    if (dB > gainControl.getMaximum()) dB = gainControl.getMaximum();

                    gainControl.setValue(dB);
                    System.out.println("ปรับเสียง " + name + " เป็น: " + dB + " dB"); // ใส่ไว้เช็ค Log
                }
            }
        });
    }
}

