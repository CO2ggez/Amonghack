package network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import util.*;

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

    private final JScrollPane terminalScroll;

    // ตำแหน่งเริ่มพิมพ์หลัง prompt
    private int inputStart = 0;

    private final IPManager ipManager;

    private boolean systemUpdating = false;


    public TerminalMinigame(MinigameManager manager) {
        this.manager = manager;
        this.ipManager = new IPManager();
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
        terminalArea.setFont(FontUtil.THAI.deriveFont(44f));
        terminalArea.setLineWrap(false);
        terminalArea.setWrapStyleWord(false);
        terminalArea.setMargin(new Insets(0, 0, 0, 0));
        terminalArea.setBorder(null);
        terminalScroll.

        ((AbstractDocument) terminalArea.getDocument()).setDocumentFilter(new TerminalDocumentFilter());

        terminalScroll = new JScrollPane(
                terminalArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        terminalScroll.setBorder(null);
        terminalScroll.setOpaque(false);
        terminalScroll.getViewport().setOpaque(false);
        terminalScroll.setWheelScrollingEnabled(true);

// ทำให้พื้นหลัง scrollbar ไม่ขาว
        terminalScroll.getVerticalScrollBar().setOpaque(false);
        terminalScroll.getHorizontalScrollBar().setOpaque(false);

        terminalScroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(terminalScroll);

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
                    refreshTerminalView();
                });
            }
        });

        // ข้อความเริ่มต้น
        runSystemUpdate(() -> terminalArea.setText(
                "=== GAME COMPANY INTERNAL SERVER TERMINAL ===\n" +
                        "Server Status: OFFLINE\n" +
                        "Network: DISCONNECTED\n" +
                        "---------------------------------------------\n" +
                        "Type 'help' for available commands.\n"
        ));

        appendPrompt();

        SwingUtilities.invokeLater(() -> {
            terminalArea.requestFocusInWindow();
            refreshTerminalView();
        });
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

        terminalScroll.setBounds(
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

        String lower = command.toLowerCase();

        if (lower.equals("help")) {
            appendLine("Available Commands:");
            appendLine("show network - แสดง IP เครื่องที่ต้องตั้ง");
            appendLine("set ip <address> - ตั้งค่า IP ให้ตรง");
            appendLine("clear - ล้างหน้าจอ");
            appendLine("exit - ปิด terminal");
            return false;
        }

        if (lower.equals("show network")) {
            ipManager.revealNetwork();
            appendLine("=== ACTIVE MACHINE ===");
            appendLine("Target IP: " + ipManager.getTargetIp());
            appendLine("Subnet: " + ipManager.getSubnet());
            appendLine("Gateway: " + ipManager.getGateway());
            appendLine("Current IP: " + ipManager.getCurrentIp());
            return false;
        }

        if (lower.startsWith("set ip")) {
            return handleSetIp(command);
        }

        if (lower.equals("clear")) {
            terminalArea.setText("");
            return false;
        }

        if (lower.equals("exit")) {
            manager.closeGame();
            return true;
        }

        appendLine("Unknown command: " + command);
        return false;
    }

    private boolean handleSetIp(String command) {
        String[] parts = command.trim().split("\\s+");

        if (parts.length != 3) {
            appendLine("Usage: set ip <address>");
            return false;
        }

        if (!ipManager.isNetworkRevealed()) {
            appendLine("Error: use 'show network' before setting IP.");
            return false;
        }

        String newIp = parts[2];

        if (!isValidIp(newIp)) {
            appendLine("Error: invalid IP address format.");
            return false;
        }

        ipManager.setCurrentIp(newIp);

        if (ipManager.isCorrectIp()) {
            appendLine("IP updated: " + newIp);
            appendLine("Connection established.");
            appendLine("Mission complete.");
            manager.onWin();
            return true;
        }

        appendLine("IP updated: " + newIp);
        appendLine("Connection failed. Incorrect IP.");
        return false;
    }

    private boolean isValidIp(String ip) {
        String[] nums = ip.split("\\.");
        if (nums.length != 4) {
            return false;
        }

        for (String num : nums) {
            try {
                int value = Integer.parseInt(num);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private void appendLine(String text) {
        runSystemUpdate(() -> terminalArea.append(text + "\n"));
        moveCaretToEnd();
    }

    private void appendPrompt() {
        runSystemUpdate(() -> terminalArea.append(PROMPT));
        inputStart = terminalArea.getDocument().getLength();
        moveCaretToEnd();
    }

    private void moveCaretToEnd() {
        SwingUtilities.invokeLater(() ->
                terminalArea.setCaretPosition(terminalArea.getDocument().getLength())
        );
    }

    private void refreshTerminalView() {
        updateLayoutBounds();

        terminalArea.revalidate();
        terminalArea.repaint();

        terminalScroll.revalidate();
        terminalScroll.repaint();

        panel.revalidate();
        panel.repaint();

        moveCaretToEnd();
    }

    private void runSystemUpdate(Runnable action) {
        systemUpdating = true;
        try {
            action.run();
        } finally {
            systemUpdating = false;
        }
    }

    private class TerminalDocumentFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {

            if (systemUpdating) {
                super.insertString(fb, offset, string, attr);
                return;
            }

            int safeOffset = Math.max(offset, inputStart);
            super.insertString(fb, safeOffset, string, attr);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {

            if (systemUpdating) {
                super.remove(fb, offset, length);
                return;
            }

            int docLength = fb.getDocument().getLength();
            int start = Math.max(offset, inputStart);
            int end = Math.min(offset + length, docLength);

            if (end <= start) {
                return;
            }

            super.remove(fb, start, end - start);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {

            if (systemUpdating) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }

            int docLength = fb.getDocument().getLength();
            int start = Math.max(offset, inputStart);
            int end = Math.min(offset + length, docLength);
            int safeLength = Math.max(0, end - start);

            if (length == 0) {
                super.replace(fb, start, 0, text, attrs);
                return;
            }

            if (safeLength == 0 && (text == null || text.isEmpty())) {
                return;
            }

            super.replace(fb, start, safeLength, text, attrs);
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}