package org.sg.models;

import org.sg.utils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

public class WinCombination {
    public final String name;
    public final double rewardMultiplier;
    public final int count;
    public final String group;
    public final When when;
    public List<List<CoveredArea>> coveredAreas;


    public WinCombination(String name, double rewardMultiplier, int count, String group, String when, List<List<String>> coveredAreas) {
        this.name = name;
        this.rewardMultiplier = rewardMultiplier;
        this.count = count;
        this.group = group;
        this.when = When.fromString(when);

        if(coveredAreas != null && coveredAreas.size() >0) {
            this.coveredAreas = new ArrayList<>();
            for(List<String> ls : coveredAreas) {
                List<CoveredArea> lsCover = new ArrayList<>();
                for(String s : ls) {
                    String[] ar = s.split(":");
                    if(ar.length >= 2) {
                        lsCover.add(new CoveredArea(ConvertUtils.toInt(ar[0], -1), ConvertUtils.toInt(ar[1], -1)));
                    }
                }
                this.coveredAreas.add(lsCover);
            }
        }
    }

    public static class CoveredArea {
        public int row;
        public int col;
        public CoveredArea(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    public enum When {
        SAME_SYMBOLS("same_symbols"),
        LINEAR_SYMBOLS("linear_symbols"),
        UNKNOWN("unknown");
        private final String value;
        When(String value) {
            this.value = value;
        }

        public static When fromString(String s) {
            for(When w : When.values()) {
                if(w.value.equalsIgnoreCase(s)) {
                    return w;
                }
            }
            return UNKNOWN;
        }
    }

    public double caclAward(int betAmount) {
        return betAmount * rewardMultiplier;
    }
}
