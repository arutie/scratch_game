package org.sg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.sg.config.Config;
import org.sg.models.Reward;
import org.sg.models.ScratchGameConfig;
import org.sg.models.Symbol;
import org.sg.models.WinCombination;
import org.sg.utils.JsonUtils;
import org.sg.utils.ObjectUtils;
import org.sg.utils.RandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScratchGame {
    @JsonIgnore
    public final ScratchGameConfig gameConfig;

    public ScratchGame(String configFile) throws Exception {
        gameConfig = Config.loadConfig(configFile);
    }

    private int betAmount;
    private Symbol[][] results;
    private Symbol bonusSymbol;
    public void start(int betAmount) {
        this.betAmount = betAmount;

        // init & random tableResult
        results = new Symbol[gameConfig.table.rows][gameConfig.table.columns];
        for(int row = 0; row < gameConfig.table.rows; ++row) {
            for(int col = 0; col < gameConfig.table.columns; ++col) {
                results[row][col] = gameConfig.table.getCell(row, col).randomSymbol();
            }
        }
        // random bonus symbol
        bonusSymbol = gameConfig.randomBonusSymbol();
        results[RandomUtils.nextInt(gameConfig.table.rows)][RandomUtils.nextInt(gameConfig.table.columns)] = bonusSymbol;
//        printTableResult();

        Map<Symbol, List<WinCombination>> mSymbolWinCombination = calcWinCombination();
//        printMapWinCombination(mSymbolWinCombination);

        double reward = calcReward(mSymbolWinCombination, betAmount);
//        printReward(reward);

        printResult(results, mSymbolWinCombination, reward);
    }

    Map<Symbol, List<WinCombination>> calcWinCombination() {
        Map<Symbol, Integer> mSameSymbol = getSameSymbol();
//        printMapSameSymbol(mSameSymbol);

        Map<Symbol, List<WinCombination>> mSymbolWinCombination = new HashMap<>();
        for(Map.Entry<Symbol, Integer> entry : mSameSymbol.entrySet()) {
            List<WinCombination> ls = getSymbolWinCombination(entry.getKey(), entry.getValue());
            if(!ObjectUtils.isEmpty(ls)) {
                mSymbolWinCombination.put(entry.getKey(), ls);
            }
        }

        return mSymbolWinCombination;
    }

    Map<Symbol, Integer> getSameSymbol() {
        Map<Symbol, Integer> m = new HashMap<>();
        for(int row = 0; row < gameConfig.table.rows; ++row) {
            for(int col = 0; col < gameConfig.table.columns; ++col) {
                if(results[row][col] == null) {
                    continue;
                }
                Integer count = m.get(results[row][col]);
                if(count == null) {
                    count = 0;
                }
                count++;
                m.put(results[row][col], count);
            }
        }
        return m.entrySet().stream()
                .filter(symbolIntegerEntry -> symbolIntegerEntry.getValue() >= gameConfig.winCombinationSameSymbolMin)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    List<WinCombination> getSymbolWinCombination(Symbol symbol, int count) {
        List<WinCombination> ls = new ArrayList<>();

        for(WinCombination winCombination : gameConfig.winSameCombinations) {
            if(count >= winCombination.count) {
                ls.add(winCombination);
                break;
            }
        }
        for(WinCombination winCombination : gameConfig.winLinearCombinations) {
            if(linearMatched(symbol, winCombination)) {
                ls.add(winCombination);
            }
        }
        return ls;
    }

    boolean linearMatched(Symbol symbol, WinCombination winCombination) {
        for(List<WinCombination.CoveredArea> lsCoveredArea : winCombination.coveredAreas) {
            if(ObjectUtils.isEmpty(lsCoveredArea)) {
                continue;
            }
            for(WinCombination.CoveredArea area : lsCoveredArea) {
                if(!results[area.row][area.col].equals(symbol)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    double calcReward(Map<Symbol, List<WinCombination>> mSymbolWinCombination, int betAmount) {
        double totalReward = 0;
        for(Map.Entry<Symbol, List<WinCombination>> entry : mSymbolWinCombination.entrySet()) {
            double multiplier = 0;
            for(WinCombination winCombination : entry.getValue()) {
                multiplier += winCombination.rewardMultiplier;
            }
            totalReward += entry.getKey().reward.calcReward(betAmount) * multiplier;
        }
        if(totalReward > 0) {
            if (bonusSymbol.reward.type == Reward.Type.MUTIPLIER) {
                totalReward *= bonusSymbol.reward.value;
            } else if (bonusSymbol.reward.type == Reward.Type.EXTRA) {
                totalReward += bonusSymbol.reward.value;
            }
        }
        return totalReward;
    }

    // <editor-fold defaultstate="collapsed" desc="utils">
    public void printTableResult() {
        System.out.println("-------------- gameTableResult --------------");
        System.out.println("[");
        for(int row = 0; row < results.length; ++row) {
            String s = "";
            for (int col = 0; col < results[0].length; ++col) {
                if(results[row][col] != null) {
                    s += "," + JsonUtils.serialize(results[row][col].name);
                } else {
                    s += ",null";
                }
            }
            if(s.length() > 0) {
                s = "[" + s.substring(1) + "]";
                System.out.println(s);
            }
        }
        System.out.println("]");
        System.out.println("-------------- gameTableResult --------------");
    }

    void printMapSameSymbol(Map<Symbol, Integer> mSameSymbol) {
        System.out.println("----- same symbol reward ------");
        for(Map.Entry<Symbol, Integer> entry : mSameSymbol.entrySet()) {
            System.out.println(entry.getKey().name + ": " + entry.getValue());
        }
        System.out.println("----- same symbol reward ------");
    }

    void printMapWinCombination(Map<Symbol, List<WinCombination>> mSymbolReward) {
        System.out.println("----- MapWinCombination ------");
        for(Map.Entry<Symbol, List<WinCombination>> entry : mSymbolReward.entrySet()) {
            System.out.println(entry.getKey().name + ": " + JsonUtils.serialize(entry.getValue()));
        }
        System.out.println("----- MapWinCombination ------");
    }

    void printReward(double reward) {
        System.out.println("----- Reward ------");
        System.out.println(reward);
        System.out.println("----- Reward ------");
    }

    void printResult(Symbol[][] matrixResult, Map<Symbol, List<WinCombination>> mSymbolList, double reward) {
        System.out.println("```json");
        System.out.println("{");
        System.out.println("\t\"matrix\": [");
        for(int row = 0; row < results.length; ++row) {
            String s = "";
            for (int col = 0; col < results[0].length; ++col) {
                if(results[row][col] != null) {
                    s += "," + JsonUtils.serialize(results[row][col].name);
                } else {
                    s += ",null";
                }
            }
            if(s.length() > 0) {
                s = "\t\t[" + s.substring(1) + "]";
                System.out.println(s);
            }
        }
        System.out.println("\t],");
        if(mSymbolList.size() > 0) {
            System.out.println("\t\"reward\": " + reward + ",");
            System.out.println("\t\"applied_winning_combinations\": {");
            String s = "";
            for (Map.Entry<Symbol, List<WinCombination>> entry : mSymbolList.entrySet()) {
                s += "\t\t\"" + entry.getKey().name + "\": [";
                String tmp = "";
                for (WinCombination winCombination : entry.getValue()) {
                    tmp += ", \"" + winCombination.name + "\"";
                }
                s += tmp.substring(2) + "],\n";
            }
            System.out.println(s.substring(0, s.length() - 2));
            if (bonusSymbol.reward.type == Reward.Type.MISS) {
                System.out.println("\t}");
            } else {
                System.out.println("\t},");
                System.out.println("\t\"applied_bonus_symbol\": \"" + bonusSymbol.name + "\"");
            }
        } else {
            System.out.println("\t\"reward\": 0");
        }
        System.out.println("}");
        System.out.println("```");
    }
    // </editor-fold>
}
