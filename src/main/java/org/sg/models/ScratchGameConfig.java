package org.sg.models;

import org.sg.utils.ObjectUtils;
import org.sg.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ScratchGameConfig {
    List<Symbol> symbolList = new ArrayList<>();
    int totalBonusProbability = 0;
    List<Probability> bonusProbabilities = new ArrayList<>();
    public List<WinCombination> winSameCombinations = new ArrayList<>();
    public List<WinCombination> winLinearCombinations = new ArrayList<>();
    public int winCombinationSameSymbolMin = Integer.MAX_VALUE;
    public Table table;
    public ScratchGameConfig(Table table) {
        this.table = table;
    }

    public void addSymbol(Symbol symbol) {
        symbolList.add(symbol);
    }

    public void addBonusProbability(Probability probability) {
        totalBonusProbability += probability.probability;
        bonusProbabilities.add(probability);
    }

    public void addWinCombination(WinCombination winCombination) {
        if(winCombination.when == WinCombination.When.UNKNOWN) {
            return;
        }

        if(winCombination.when == WinCombination.When.SAME_SYMBOLS) {
            winCombinationSameSymbolMin = Math.min(winCombinationSameSymbolMin, winCombination.count);
            winSameCombinations.add(winCombination);
            winSameCombinations.sort(Comparator.comparingInt(o -> ((WinCombination)o).count).reversed());
        } else if(!ObjectUtils.isEmpty(winCombination.coveredAreas)) {
            for(List<WinCombination.CoveredArea> ls : winCombination.coveredAreas) {
                winCombinationSameSymbolMin = (ls == null || ls.size() == 0) ? winCombinationSameSymbolMin : Math.min(winCombinationSameSymbolMin, ls.size());
            }
            winLinearCombinations.add(winCombination);
        }
    }

    public Symbol getSymbol(String name) {
        Optional<Symbol> op = symbolList.stream().filter(symbol -> symbol.name.equalsIgnoreCase(name)).findFirst();
        return op.isPresent() ? op.get() : null;
    }

    public Symbol randomBonusSymbol() {
        if(totalBonusProbability <= 0) {
            return null;
        }
        int rnd = RandomUtils.nextInt(totalBonusProbability);
        int tmp = 0;
        for(Probability probability : bonusProbabilities) {
            tmp += probability.probability;
            if(tmp > rnd) {
                return probability.symbol;
            }
        }
        return bonusProbabilities.get(bonusProbabilities.size() - 1).symbol;
    }
}
