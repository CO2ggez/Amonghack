package network;

import java.util.Random;

public class IPManager {
    private final String targetIp;
    private String currentIp = "not set";
    private final String subnet = "255.255.255.0";
    private final String gateway = "192.168.1.1";
    private boolean networkRevealed = false;

    public IPManager() {
        this.targetIp = randomIp();
    }

    private String randomIp() {
        int host = new Random().nextInt(200) + 20;
        return "192.168.1." + host;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    public String getSubnet() {
        return subnet;
    }

    public String getGateway() {
        return gateway;
    }

    public boolean isNetworkRevealed() {
        return networkRevealed;
    }

    public void revealNetwork() {
        networkRevealed = true;
    }

    public void setCurrentIp(String currentIp) {
        this.currentIp = currentIp;
    }

    public boolean isCorrectIp() {
        return targetIp.equals(currentIp);
    }
}