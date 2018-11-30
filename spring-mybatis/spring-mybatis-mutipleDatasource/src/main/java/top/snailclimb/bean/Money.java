package top.snailclimb.bean;

public class Money {
    private int basic;
    private int reward;
    private int punishment;

    public void setBasic(int basic) {
        this.basic = basic;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public void setPunishment(int punishment) {
        this.punishment = punishment;
    }

    public int getBasic() {
        return basic;
    }

    public int getReward() {
        return reward;
    }

    public int getPunishment() {
        return punishment;
    }

    @Override
    public String toString() {
        return "Money{" +
                "basic=" + basic +
                ", reward=" + reward +
                ", punishment=" + punishment +
                '}';
    }
}
