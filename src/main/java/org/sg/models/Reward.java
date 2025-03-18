package org.sg.models;

public class Reward {
    public final Type type;
    public final double value;

    public Reward(Type type, double value) {
        this.type = type;
        this.value = value;
    }

    public double calcReward(int betAmount) {
        switch (type) {
            case MUTIPLIER -> {return betAmount * value;}
            case EXTRA -> {return betAmount + value;}
        }
        return 0;
    }

    public enum Type {
        MISS(0),
        MUTIPLIER(1),
        EXTRA(2);
        final int value;
        Type(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
}
