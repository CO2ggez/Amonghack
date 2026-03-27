package network;

import java.util.HashMap;
import java.util.Map;

public class LogManager {

    public static class DayLogData {
        private final String terminalHeader;
        private final String fullLog;
        private final String correctBanIp;
        private final String correctDns;

        public DayLogData(String terminalHeader, String fullLog, String correctBanIp, String correctDns) {
            this.terminalHeader = terminalHeader;
            this.fullLog = fullLog;
            this.correctBanIp = correctBanIp;
            this.correctDns = correctDns;
        }

        public String getTerminalHeader() {
            return terminalHeader;
        }

        public String getFullLog() {
            return fullLog;
        }

        public String getCorrectBanIp() {
            return correctBanIp;
        }

        public String getCorrectDns() {
            return correctDns;
        }
    }

    private static final Map<Integer, DayLogData> DAY_LOGS = new HashMap<>();

    static {
        DAY_LOGS.put(1, new DayLogData(
                """
                DAY 01
                === AMONGHACK COMPANY INTERNAL SERVER TERMINAL ===
                Server Status:
                NORMAL
                Network:
                STABLE
                ---------------------------------------
                Active Alerts: 1 | Review logs for details.
                Type 'help' for available commands.
                """,
                """
                SERVER-01:/var/log# read log
                [ SERVER LOG ] Date: DAY-01
                ---------------------------------------------
        
                14:22:41
                [INFO]
                192.168.10.72 (OFFICE-PC-02) user login.
        
                14:45:03
                [INFO]
                File upload initiated.
        
                14:45:03
                >> Source: 192.168.10.72 (OFFICE-PC-02)
                >> File: project_final_v3.psd
        
                14:45:10
                [WARN]
                File redirected to temporary directory.
        
                >> Destination: 192.168.10.10 (SERVER-01)
                /tmp/cache_4521.bin
        
                14:45:12
                [INFO]
                Upload completed.
        
                00:01:03
                [INFO]
                192.168.10.10 (SERVER-01) online. Services nominal.
        
                00:10:22
                [INFO]
                Keycard ID:0003 (LUCA) - access granted, Floor 2.
        
                00:44:51
                [INFO]
                192.168.10.72 (OFFICE-PC-02) shutdown detected.
        
                01:52:14
                [WARN]
                Unverified file detected in temp storage.
        
                >> File: /tmp/cache_4521.bin
                >> Source (previous): 192.168.10.72
        
                02:30:40
                [INFO]
                Auto backup completed.
        
                04:58:01
                [INFO]
                No further anomalies detected.
        
                05:59:50
                [INFO]
                Shift end.
        
                -- END OF LOG: DAY-01 --
                """,
                "",
                ""
        ));

        DAY_LOGS.put(2, new DayLogData(
                """
                DAY 02
                === AMONGHACK COMPANY INTERNAL SERVER TERMINAL ===
                Server Status:
                WARNING
                Network:
                LIMITED CONNECTION
                ---------------------------------------------
                หากพบIP/DNSแปลกปลอม(Warning)ให้ทำการbanหรือblockได้เลยทันที
                Active Alerts: 3 | Review logs for details.
                """,
                """
                SERVER-01:/var/log# read log
                [ SERVER LOG ] Date: DAY-02
                ---------------------------------------------

                16:11:09
                [INFO]
                192.168.10.72 (OFFICE-PC-02) active session detected.

                18:42:33
                [WARN]
                System idle timeout bypassed.

                >> Device remained active without user input.

                00:39:18
                [WARN]
                192.168.10.72 (OFFICE-PC-02) network configuration modified.

                >> IP: 192.168.10.72 -> 192.168.10.201
                >> Subnet: 255.255.255.0

                00:39:20
                [WARN]
                DNS modified.

                >> DNS: 8.8.8.8 -> 45.77.214.132

                01:12:44
                [WARN]
                Unknown LAN cable detected.

                >> Trace:
                192.168.10.10 (SERVER-01)
                -> SWITCH-01 -> PORT-07 -> OFFICE zone

                02:28:11
                [INFO]
                192.168.10.201 (OFFICE-PC-02)
                >> LIMITED CONNECTION

                03:47:02
                [WARN]
                Outbound connection attempt.

                05:59:50
                [INFO]
                Shift end.

                -- END OF LOG: DAY-02 --
                """,
                "192.168.10.201",
                "45.77.214.132"
        ));

        DAY_LOGS.put(3, new DayLogData(
                """
                DAY 03
                === AMONGHACK COMPANY INTERNAL SERVER TERMINAL ===
                Server Status:
                CRITICAL
                Network:
                UNSTABLE
                ---------------------------------------------
                หากพบIP/DNSแปลกปลอม(Warning)ให้ทำการbanหรือblockได้เลยทันที
                Active Alerts: 4 | Review logs for details.
                """,
                """
                SERVER-01:/var/log# read log
                [ SERVER LOG ] Date: DAY-03
                ---------------------------------------------

                02:59:58
                [INFO]
                System stable.

                03:00:02
                [ERROR]
                Power loss detected.

                03:00:03
                [INFO]
                Switching to backup power...

                03:00:05
                [WARN]
                Unauthorized connection attempt.

                >> Target: 192.168.10.10 (SERVER-01)
                >> Source: UNKNOWN DEVICE

                03:00:06
                [WARN]
                Source trace recovered.

                >> IP: 192.168.10.166

                03:00:07
                [WARN]
                Connection retry (1)

                03:00:09
                [WARN]
                Connection retry (2)

                03:00:10
                [WARN]
                DNS failover table modified during outage.

                >> DNS: 1.1.1.1 -> 91.210.166.77

                03:00:12
                [INFO]
                Power restored.

                03:00:13
                [WARN]
                Connection terminated unexpectedly.

                03:00:18
                [WARN]
                Suspicious timing:
                >> intrusion synced with power outage

                05:59:50
                [INFO]
                Shift end.

                -- END OF LOG: DAY-03 --
                """,
                "192.168.10.166",
                "91.210.166.77"
        ));

        DAY_LOGS.put(4, new DayLogData(
                """
                DAY 04
                === AMONGHACK COMPANY INTERNAL SERVER TERMINAL ===
                Server Status:
                DANGER
                Network:
                COMPROMISED
                ---------------------------------------------
                หากพบIP/DNSแปลกปลอม(Warning)ให้ทำการbanหรือblockได้เลยทันที
                Active Alerts: 5 | Review logs for details.
                """,
                """
                SERVER-01:/var/log# read log
                [ SERVER LOG ] Date: DAY-04
                ---------------------------------------------

                15:14:11
                [INFO]
                Routine maintenance logged.

                18:02:55
                [WARN]
                Unregistered device briefly detected (auto-hidden).

                00:48:22
                [WARN]
                Residual connection detected.

                01:18:22
                [ERROR]
                Unknown device detected.

                >> IP: 192.168.10.250
                >> Type: BRIDGE
                >> MAC: AA:BB:CC:01:02:03

                01:18:23
                [WARN]
                Traffic redirection detected.

                >> 192.168.10.10 (SERVER-01)
                -> 192.168.10.250
                -> EXTERNAL

                01:18:24
                [CRITICAL]
                DNS redirect rule injected.

                >> DNS: 8.8.8.8 -> 185.44.7.23

                01:18:26
                [WARN]
                Resolver response mismatch detected.

                02:44:10
                [WARN]
                Unauthorized data access.

                04:59:59
                [CRITICAL]
                Data exfiltration in progress.

                05:59:50
                [INFO]
                Shift end.

                -- END OF LOG: DAY-04 --
                """,
                "192.168.10.250",
                "185.44.7.23"
        ));
    }

    public DayLogData getDayLog(int day) {
        return DAY_LOGS.getOrDefault(day, DAY_LOGS.get(1));
    }
}