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

public class BanIpLogMinigame implements Minigame {
    private static final int WINDOW_WIDTH = 1100;
    private static final String PROMPT = "SERVER-01:/var/log# ";

    private final JPanel panel;
    private final MinigameManager manager;
    private final Image terminalImage;
    private final Rectangle closeBounds = new Rectangle();
    private final Rectangle terminalBounds = new Rectangle();
    private final Rectangle contentBounds = new Rectangle();
    private final JTextArea terminalArea;
    private final JScrollPane terminalScroll;
    private final LogManager.DayLogData dayLogData;

    private int inputStart = 0;
    private boolean systemUpdating = false;
    private boolean hasReadLog = false;
    private boolean ipSolved = false;
    private boolean dnsSolved = false;

    private boolean missionCompleted = false;

    public BanIpLogMinigame(MinigameManager manager, int day) {
        this.manager = manager;
        this.dayLogData = new LogManager().getDayLog(day);
        this.ipSolved = dayLogData.getCorrectBanIp() == null || dayLogData.getCorrectBanIp().isBlank();
        this.dnsSolved = dayLogData.getCorrectDns() == null || dayLogData.getCorrectDns().isBlank();
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
        terminalArea.setOpaque(false);
        terminalArea.setForeground(new Color(0, 255, 70));
        terminalArea.setCaretColor(new Color(0, 255, 70));
        terminalArea.setFont(new Font("Tahoma", Font.PLAIN, 22));
        terminalArea.setLineWrap(false);
        terminalArea.setWrapStyleWord(false);
        terminalArea.setMargin(new Insets(0, 0, 0, 0));
        terminalArea.setBorder(null);

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
        terminalScroll.getVerticalScrollBar().setOpaque(false);
        terminalScroll.getHorizontalScrollBar().setOpaque(false);
        terminalScroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(terminalScroll);

        terminalArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int caret = terminalArea.getCaretPosition();
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

        terminalArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    terminalArea.requestFocusInWindow();
                    moveCaretToEnd();
                });
            }
        });

        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && panel.isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    terminalArea.requestFocusInWindow();
                    refreshTerminalView();
                });
            }
        });

        runSystemUpdate(() -> terminalArea.setText(dayLogData.getTerminalHeader()));
        appendPrompt();

        SwingUtilities.invokeLater(() -> {
            terminalArea.requestFocusInWindow();
            refreshTerminalView();
        });
    }

    private void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        g2.drawImage(terminalImage, terminalBounds.x, terminalBounds.y, terminalBounds.width, terminalBounds.height, null);
        g2.dispose();
    }

    private void updateLayoutBounds() {
        int drawWidth = Math.min(WINDOW_WIDTH, panel.getWidth() - 120);
        int drawHeight = (drawWidth * 1080) / 1920;

        if (drawHeight > panel.getHeight() - 120) {
            drawHeight = panel.getHeight() - 120;
            drawWidth = (drawHeight * 1920) / 1080;
        }

        int drawX = (panel.getWidth() - drawWidth) / 2;
        int drawY = (panel.getHeight() - drawHeight) / 2;

        terminalBounds.setBounds(drawX, drawY, drawWidth, drawHeight);

        int closeX = drawX + (int) (drawWidth * 0.93);
        int closeY = drawY + (int) (drawHeight * 0.005);
        int closeW = (int) (drawWidth * 0.055);
        int closeH = (int) (drawHeight * 0.055);
        closeBounds.setBounds(closeX, closeY, closeW, closeH);

        int contentX = drawX + (int) (drawWidth * 0.03);
        int contentY = drawY + (int) (drawHeight * 0.08);
        int contentW = (int) (drawWidth * 0.94);
        int contentH = (int) (drawHeight * 0.87);
        contentBounds.setBounds(contentX, contentY, contentW, contentH);

        terminalScroll.setBounds(contentBounds.x, contentBounds.y, contentBounds.width, contentBounds.height);
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

    private boolean runCommand(String command) {
        if (command.isEmpty()) {
            return false;
        }

        String lower = command.toLowerCase().trim();

        if (lower.equals("help")) {
            appendLine("Available Commands:");
            appendLine("read log - เปิดดู server log");
            appendLine("ban ip <address> - แบน IP ที่น่าสงสัย");
            appendLine("block dns <address> - บล็อก DNS ที่ถูกแก้ไข");
            appendLine("clear - ล้างหน้าจอ");
            appendLine("exit - ปิด terminal");
            return false;
        }

        if (lower.equals("read log")) {
            hasReadLog = true;
            appendLine(dayLogData.getFullLog());

            if (ipSolved && dnsSolved) {
                appendLine("No direct threat found.");
                appendLine("Mission complete.");
                manager.onWin();
                return true;
            }

            return false;
        }

        if (lower.startsWith("ban ip")) {
            return handleBanIp(command);
        }

        if (lower.startsWith("block dns")) {
            return handleBlockDns(command);
        }

        if (lower.equals("clear")) {
            terminalArea.setText("");
            return false;
        }

        if (lower.equals("exit")) {
            manager.closeGame();
            if(missionCompleted && manager.getGamePanel().getGSM().getCurrentDay() == 4){
                manager.getGamePanel().dialogBox.startDialog(ui.StoryDialog.DAY4_LOG );
            }
            return true;
        }

        appendLine("Unknown command: " + command);
        return false;
    }

    private boolean handleBanIp(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 3) {
            appendLine("Usage: ban ip <address>");
            return false;
        }

        if (!hasReadLog) {
            appendLine("Error: use 'read log' before banning IP.");
            return false;
        }

        String ip = parts[2];
        if (!isValidIp(ip)) {
            appendLine("Error: invalid IP address format.");
            return false;
        }

        if (ip.equals(dayLogData.getCorrectBanIp())) {
            ipSolved = true;
            appendLine("IP banned successfully: " + ip);
            return checkMissionComplete();
        }

        appendLine("Failed to ban IP: no matching threat found.");
        return false;
    }

    private boolean handleBlockDns(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 3) {
            appendLine("Usage: block dns <address>");
            return false;
        }

        if (!hasReadLog) {
            appendLine("Error: use 'read log' before blocking DNS.");
            return false;
        }

        String dns = parts[2];
        if (!isValidIp(dns)) {
            appendLine("Error: invalid DNS format.");
            return false;
        }

        if (dns.equals(dayLogData.getCorrectDns())) {
            dnsSolved = true;
            appendLine("DNS blocked successfully: " + dns);
            return checkMissionComplete();
        }

        appendLine("Failed to block DNS: no matching entry found.");
        return false;
    }

    private boolean checkMissionComplete() {
        if (!ipSolved || !dnsSolved) {
            appendLine("Partial containment successful.");
            return false;
        }

        appendLine("Threat neutralized.");
        appendLine("Mission complete.");
        appendLine("Type 'exit' to close terminal.");

        missionCompleted = true;
        manager.onWinStayOpen();
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
        SwingUtilities.invokeLater(() -> terminalArea.setCaretPosition(terminalArea.getDocument().getLength()));
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
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (systemUpdating) {
                super.insertString(fb, offset, string, attr);
                return;
            }
            int safeOffset = Math.max(offset, inputStart);
            super.insertString(fb, safeOffset, string, attr);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
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
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
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