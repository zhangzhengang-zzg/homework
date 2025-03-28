package zzg.homework;

import java.util.Objects;

public class Stake {
    private int customerId;
    private int stakeAmount;

    public void setStakeAmount(int stakeAmount) {
        this.stakeAmount = stakeAmount;
    }

    public Stake(int customerId, int stakeAmount) {
        this.customerId = customerId;
        this.stakeAmount = stakeAmount;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getStakeAmount() {
        return stakeAmount;
    }

    @Override
    public String toString() {
        return customerId + "=" + stakeAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stake stake = (Stake) o;
        return customerId == stake.customerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}
