package zzg.homework;

public class Session {
    private String key;
    private long expirationTime;

    Session(String key) {
        this.key = key;
        this.expirationTime = System.currentTimeMillis() + BettingServer.SESSION_DURATION;
    }

    public boolean invalid() {
        return System.currentTimeMillis() >= expirationTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
