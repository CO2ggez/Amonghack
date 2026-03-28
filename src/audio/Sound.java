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

        loadSound("walk", "/audio/SoundWAV/walk.wav",1f);
        loadSound("walk2", "/audio/SoundWAV/walk2.wav",1f);
        loadSound("door", "/audio/SoundWAV/door.wav",1f);
        loadSound("bg1", "/audio/SoundWAV/bg1.wav",0.3f);
        loadSound("ringLift", "/audio/SoundWAV/ringLift.wav",0.5f);
        loadSound("click1", "/audio/SoundWAV/click1.wav",1f);
        loadSound("connect", "/audio/SoundWAV/connect.wav",1f);
        loadSound("connect2", "/audio/SoundWAV/connect2.wav",1f);
        loadSound("menu_bg", "/audio/SoundWAV/menu_bg.wav",0.5f);
        loadSound("stair", "/audio/SoundWAV/stair.wav",2f);
        loadSound("success", "/audio/SoundWAV/success.wav",1f);
        loadSound("bg_cg_day0", "/audio/SoundWAV/bg_cg_day0.wav",0.05f);
        loadSound("bg_dayEnd", "/audio/SoundWAV/bg_dayEnd.wav",0.5f);
        loadSound("ending", "/audio/SoundWAV/ending.wav",0.5f);
        loadSound("ringtone", "/audio/SoundWAV/ringtone.wav",1f);
    }

    private void loadSound(String name, String path, float defaultVolume) {
        soundWorker.execute(() -> {
            try {
                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) return;

                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);

                // --- เซตระดับเสียงทันทีหลังโหลดเสร็จ ---
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float dB = (float) (Math.log(defaultVolume) / Math.log(10.0) * 20.0);

                    // Clamp ค่าให้อยู่ในระยะที่รองรับ
                    if (dB < gainControl.getMinimum()) dB = gainControl.getMinimum();
                    if (dB > gainControl.getMaximum()) dB = gainControl.getMaximum();

                    gainControl.setValue(dB);
                }

                clips.put(name, clip);
                System.out.println("โหลดและเซตเสียงสำเร็จ: " + name + " (" + defaultVolume + ")");

            } catch (Exception e) {
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

