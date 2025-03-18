package org.sg.models;

import org.sg.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private int totalProbability = 0;
    private final List<Probability> probabilities;
//    private Symbol symbol;
    public Cell() {
        probabilities = new ArrayList<>();
    }

    public void addProbability(Probability probability) {
        probabilities.add(probability);
        totalProbability += probability.probability;
    }

    public Symbol randomSymbol() {
        if(totalProbability <= 0) {
            return null;
        }
        int rnd = RandomUtils.nextInt(totalProbability);
        int tmp = 0;
        for(Probability probability : probabilities) {
            tmp += probability.probability;
            if(tmp > rnd) {
                return probability.symbol;
            }
        }
        return probabilities.get(probabilities.size() - 1).symbol;
    }
}
