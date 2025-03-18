package org.sg.models;

public abstract class Symbol {
    public final String name;
    public final Reward reward;

    protected Symbol(String name, Reward reward) {
        this.name = name;
        this.reward = reward;
    }
    public enum Type {
        STANDARD(1),
        BONUS(2);

        final int value;
        Type(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }

        public static Type fromString(String s) {
            switch(s) {
                case "standard" -> {
                    return STANDARD;
                }
                case "bonus" -> {
                    return BONUS;
                }
            }
            return null;
        }
    }
}
