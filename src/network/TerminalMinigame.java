package network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TerminalMinigame implements Minigame {
    // ขนาด terminal ตอนแสดงบนจอ
    private static final int WINDOW_WIDTH = 1100;

    // prompt ของเครื่อง server
    private static final String PROMPT = "root@server01:/var/log# ";

    private final JPanel panel;
    private final MinigameManager manager;
    private final Image terminalImage;

    // hitbox ปุ่ม X
    private final Rectangle closeBounds = new Rectangle();

    // กรอบ terminal กลางจอ
    private final Rectangle terminalBounds = new Rectangle();

    // พื้นที่ด้านในสำหรับ text
    private final Rectangle contentBounds = new Rectangle();

    // ใช้ text area ตัวเดียว เหมือน cmd
    private final JTextArea terminalArea;

    // ตำแหน่งเริ่มพิมพ์หลัง prompt
    private int inputStart = 0;

    public TerminalMinigame(MinigameManager manager) {
        this.manager = manager;
        this.terminalImage = new ImageIcon(getClass().getResource("/ui/TerminalUI.png")).getImage();

        panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                updateLayoutBounds();
                draw(g);
            }

            @Override
            public void doLayout() {
                super.doLayout();
                updateLayoutBounds();
            }
        };

        panel.setOpaque(false);

        terminalArea = new JTextArea();
        terminalArea.setEditable(true);
        terminalArea.setOpaque(false); // ด้านใน terminal เป็นสีดำอยู่แล้ว
        terminalArea.setForeground(new Color(0, 255, 70));
        terminalArea.setCaretColor(new Color(0, 255, 70));
        terminalArea.setFont(new Font("Tahoma", Font.PLAIN, 22));
        terminalArea.setLineWrap(false);
        terminalArea.setWrapStyleWord(false);
        terminalArea.setMargin(new Insets(0, 0, 0, 0));
        terminalArea.setBorder(null);

        panel.add(terminalArea);

        // บังคับให้พิมพ์ได้เฉพาะหลัง prompt
        terminalArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int caret = terminalArea.getCaretPosition();

                // ถ้า caret หลุดไปก่อน prompt ดึงกลับไปท้ายสุด
                if (caret < inputStart) {
                    terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                        e.consume();
                        handleEnter();
                    }
                    case KeyEvent.VK_BACK_SPACE, KeyEvent.VK_LEFT -> {
                        if (terminalArea.getCaretPosition() <= inputStart) {
                            e.consume();
                        }
                    }
                    case KeyEvent.VK_HOME -> {
                        e.consume();
                        terminalArea.setCaretPosition(inputStart);
                    }
                }
            }
        });

        // คลิก X เพื่อปิด terminal
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (closeBounds.contains(e.getPoint())) {
                    manager.closeGame();
                } else {
                    terminalArea.requestFocusInWindow();
                    moveCaretToEnd();
                }
            }
        });

        // คลิกใน text area ให้ caret ไปท้ายสุด
        terminalArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    terminalArea.requestFocusInWindow();
                    moveCaretToEnd();
                });
            }
        });

        // ตอน panel แสดงครั้งแรก ให้โฟกัสที่ terminal
        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && panel.isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    terminalArea.requestFocusInWindow();
                    moveCaretToEnd();
                    terminalArea.repaint();
                    panel.repaint();
                });
            }
        });

        // ข้อความเริ่มต้น
        terminalArea.setText(
                "=== GAME COMPANY INTERNAL SERVER TERMINAL ===\n" +
                        "Server Status: OFFLINE\n" +
                        "Network: DISCONNECTED\n" +
                        "---------------------------------------------\n" +
                        "Type 'help' for available commands.\n"
        );

        appendPrompt();
    }

    private void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // ทำฉากด้านหลังมืดลง
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());

        // วาด terminal กลางจอ
        g2.drawImage(
                terminalImage,
                terminalBounds.x,
                terminalBounds.y,
                terminalBounds.width,
                terminalBounds.height,
                null
        );

        g2.dispose();
    }

    private void updateLayoutBounds() {
        int drawWidth = Math.min(WINDOW_WIDTH, panel.getWidth() - 120);
        int drawHeight = (drawWidth * 1080) / 1920;

        // กันกรณีจอเตี้ยเกิน
        if (drawHeight > panel.getHeight() - 120) {
            drawHeight = panel.getHeight() - 120;
            drawWidth = (drawHeight * 1920) / 1080;
        }

        int drawX = (panel.getWidth() - drawWidth) / 2;
        int drawY = (panel.getHeight() - drawHeight) / 2;

        terminalBounds.setBounds(drawX, drawY, drawWidth, drawHeight);

        // hitbox ปุ่ม X
        int closeX = drawX + (int) (drawWidth * 0.93);
        int closeY = drawY + (int) (drawHeight * 0.005);
        int closeW = (int) (drawWidth * 0.055);
        int closeH = (int) (drawHeight * 0.055);
        closeBounds.setBounds(closeX, closeY, closeW, closeH);

        // พื้นที่ด้านในสำหรับข้อความ
        int contentX = drawX + (int) (drawWidth * 0.03);
        int contentY = drawY + (int) (drawHeight * 0.08);
        int contentW = (int) (drawWidth * 0.94);
        int contentH = (int) (drawHeight * 0.87);
        contentBounds.setBounds(contentX, contentY, contentW, contentH);

        terminalArea.setBounds(
                contentBounds.x,
                contentBounds.y,
                contentBounds.width,
                contentBounds.height
        );
    }

    private void handleEnter() {
        String fullText = terminalArea.getText();
        String command = fullText.substring(inputStart).trim();

        terminalArea.append("\n");

        if (runCommand(command)) {
            return;
        }

        appendPrompt();
    }

    // return true ถ้าไม่ต้องขึ้น prompt ใหม่
    private boolean runCommand(String command) {
        if (command.isEmpty()) {
            return false;
        }

        switch (command.toLowerCase()) {
            case "help" -> {
                appendLine("Available Commands:");
                appendLine("status - แสดงสถานะระบบหลักของบริษัท");
                appendLine("show network - แสดงค่าปัจจุบันของเครื่องผู้เล่น");
                appendLine("set ip - เปลี่ยน ip");
                appendLine("set gateway - เปลี่ยน gateway ของระบบ");
                appendLine("list users - แสดงรายชื่อ user ทั้งหมดในองค์กร");
                appendLine("read log - เปิด log");
                appendLine("ban ip - จัดเก็บ ip ที่ไม่ทราบที่มาเข้าสู่ Blacklist");
            }
            case "status" -> {
                appendLine("Server Status: ONLINE (ONLINE / OFFLINE)");
                appendLine("Security Level: STABLE (STABLE / WARNING / BREACHED)");
                appendLine("Active Alerts: 0");
            }
            case "show network" -> {
                appendLine("IP: not set");
                appendLine("Subnet: 255.255.255.0");
                appendLine("Gateway: not set");
                appendLine("Status: DISCONNECTED");
            }
            case "set ip" -> appendLine("Usage: set ip [address]");
            case "set gateway" -> appendLine("Usage: set gateway [address]");
            case "list users" -> appendLine("User list unavailable.");
            case "read log" -> appendLine("No log loaded.");
            case "ban ip" -> appendLine("Usage: ban ip [address]");
            case "clear" -> {
                terminalArea.setText("");
            }
            case "exit" -> {
                manager.closeGame();
                return true;
            }
            default -> appendLine("Unknown command: " + command);
        }

        return false;
    }

    private void appendLine(String text) {
        terminalArea.append(text + "\n");
        moveCaretToEnd();
    }

    private void appendPrompt() {
        terminalArea.append(PROMPT);
        inputStart = terminalArea.getDocument().getLength();
        moveCaretToEnd();
    }

    private void moveCaretToEnd() {
        SwingUtilities.invokeLater(() ->
                terminalArea.setCaretPosition(terminalArea.getDocument().getLength())
        );
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}