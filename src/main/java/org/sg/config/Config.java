package org.sg.config;

import org.sg.models.ScratchGameConfig;
import org.sg.models.*;
import org.sg.models.builder.SymbolBuilder;
import org.sg.utils.ConvertUtils;
import org.sg.utils.JsonUtils;
import org.sg.utils.MapUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Config {
    public static ScratchGameConfig loadConfig(String configFile) throws Exception {
        try {
            String s = Files.readString(Paths.get(configFile));
            Map<String, Object> root = JsonUtils.deserialize(s, Map.class);
            if(root == null) {
                System.out.println("Can not parse config file");
                System.exit(2);
            }

            int rows = MapUtils.getInt(root, Constant.rows);
            int columns = MapUtils.getInt(root, Constant.rows);
            if(rows <= 0 || columns <= 0) {
                System.out.println("Config Invalid rows or columns");
                System.exit(2);
            }

            ScratchGameConfig scratchGame = new ScratchGameConfig(new Table(rows, columns));

            loadSymbols(root, scratchGame);

            loadProbabilities(root, scratchGame);

            loadWinCombinations(root, scratchGame);

            return scratchGame;
        } catch(Exception ex) {
            throw new Exception("loadConfig error: " + ex.getMessage());
        }
    }

    static void loadSymbols(Map<String, Object> root, ScratchGameConfig scratchGame) {
        Map<String, Object> symbols = (Map)MapUtils.getMissingOrNull(root, "symbols");
        if(symbols == null) {
            System.out.println("Config Invalid symbols");
            System.exit(3);
        }

        for(Map.Entry<String, Object> entry : symbols.entrySet()) {
            Symbol symbol = getSymbol(entry);
            if(symbol == null) {
                System.out.println("Config Invalid symbol: " + JsonUtils.serialize(entry));
                System.exit(4);
            }
            scratchGame.addSymbol(symbol);
        }
    }

    static void loadProbabilities(Map<String, Object> root, ScratchGameConfig scratchGame) {
        Map<String, Object> probabilities = (Map)MapUtils.getMissingOrNull(root, "probabilities");
        if(probabilities == null) {
            System.out.println("Config Invalid probabilities");
            System.exit(5);
        }
        Object o = probabilities.get("standard_symbols");
        if(o == null || !(o instanceof List)) {
            System.out.println("Config Invalid probabilities standard_symbols: " + JsonUtils.serialize(o));
            System.exit(5);
        }
        loadProbabilitiesStandards((List)o, scratchGame);

        o = probabilities.get("bonus_symbols");
        if(o == null || !(o instanceof Map)) {
            System.out.println("Config Invalid probabilities bonus_symbols: " + JsonUtils.serialize(o));
            System.exit(5);
        }
        loadProbabilitiesBonus((Map)o, scratchGame);
    }

    static void loadProbabilitiesStandards(List<Object> probStandards, ScratchGameConfig scratchGame) {
        for(Object probStandard : probStandards) {
            loadProbabilitiesStandard(probStandard, scratchGame);
        }
    }

    static void loadProbabilitiesStandard(Object probStandard, ScratchGameConfig scratchGame) {
        try {
            Map<String, Object> mProb = (Map) probStandard;
            int row = MapUtils.getInt(mProb, Constant.row, -1);
            int col = MapUtils.getInt(mProb, Constant.column, -1);
            if(row < 0 || col < 0 || row > scratchGame.table.rows || col > scratchGame.table.columns) {
                System.out.println("Config Invalid ProbabilitiesStandard: " + JsonUtils.serialize(probStandard));
                System.exit(5);
            }

            Map<String, Object> mSymbols = (Map)mProb.get(Constant.symbols);
            if(mSymbols == null) {
                System.out.println("Config Invalid ProbabilitiesStandard symbols: " + JsonUtils.serialize(probStandard));
                System.exit(5);
            }
            for(Map.Entry<String, Object> entry : mSymbols.entrySet()) {
                Symbol symbol = scratchGame.getSymbol(entry.getKey());
                int probValue = ConvertUtils.toInt(entry.getValue(), -1);
                if(symbol == null || probValue < 0) {
                    System.out.println("Config Invalid ProbabilitiesStandard symbols: " + JsonUtils.serialize(probStandard));
                    System.exit(5);
                }
                Probability probability = new Probability(symbol, probValue);
                scratchGame.table.getCell(row, col).addProbability(probability);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Config Invalid ProbabilitiesStandard: " + JsonUtils.serialize(probStandard));
            System.exit(5);
        }
    }

    static void loadProbabilitiesBonus(Object probBonus, ScratchGameConfig scratchGame) {
        try {
            Map<String, Object> mProb = (Map) probBonus;
            Map<String, Object> mSymbols = (Map)mProb.get(Constant.symbols);
            if(mSymbols == null) {
                System.out.println("Config Invalid ProbabilitiesBonus symbols: " + JsonUtils.serialize(probBonus));
                System.exit(5);
            }
            for(Map.Entry<String, Object> entry : mSymbols.entrySet()) {
                Symbol symbol = scratchGame.getSymbol(entry.getKey());
                int probValue = ConvertUtils.toInt(entry.getValue(), -1);
                if(symbol == null || probValue < 0) {
                    System.out.println("Config Invalid ProbabilitiesBonus symbols: " + JsonUtils.serialize(probBonus));
                    System.exit(5);
                }
                Probability probability = new Probability(symbol, probValue);
                scratchGame.addBonusProbability(probability);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Config Invalid ProbabilitiesStandard: " + JsonUtils.serialize(probBonus));
            System.exit(5);
        }
    }

    static void loadWinCombinations(Map<String, Object> root, ScratchGameConfig scratchGame) {
        Map<String, Object> winCombinations = (Map)MapUtils.getMissingOrNull(root, Constant.win_combinations);
        if(winCombinations == null) {
            System.out.println("Config Invalid WinCombinations");
            System.exit(6);
        }
        for(Map.Entry<String, Object> entry : winCombinations.entrySet()) {
            scratchGame.addWinCombination(loadWinCombination(entry));
        }
    }

    static WinCombination loadWinCombination(Map.Entry<String, Object> entry) {
        Map<String, Object> m = (Map)entry.getValue();
        WinCombination winCombination = new WinCombination(
                entry.getKey(),
                MapUtils.getDouble(m, Constant.reward_multiplier, 0),
                MapUtils.getInt(m, Constant.count, 9999),
                MapUtils.getMissingOrBlank(m, Constant.group),
                MapUtils.getMissingOrBlank(m, Constant.when),
                (List)MapUtils.getMissingOrNull(m, Constant.covered_areas)
        );
        return winCombination;
    }

    static Symbol getSymbol(Map.Entry<String, Object> entry) {
        try {
            Map<String, Object> m = (Map) entry.getValue();
            Symbol.Type type = Symbol.Type.fromString(MapUtils.getMissingOrBlank(m, "type"));
            if(type == null) {
                System.out.println("Config Symbol.Type invalid: " + JsonUtils.serialize(entry));
                System.exit(4);
            }
            double rwValue = 0;
            Reward.Type rwType = null;
            switch (type) {
                case STANDARD -> {
                    rwValue = MapUtils.getDouble(m, "reward_multiplier", 0);
                    rwType = Reward.Type.MUTIPLIER;
                }
                case BONUS -> {
                    String impact = MapUtils.getMissingOrBlank(m,"impact");
                    if(impact.equalsIgnoreCase("multiply_reward")) {
                        rwValue = MapUtils.getDouble(m, "reward_multiplier", 0);
                        rwType = Reward.Type.MUTIPLIER;
                    } else if(impact.equalsIgnoreCase("extra_bonus")) {
                        rwValue = MapUtils.getDouble(m, "extra", 0);
                        rwType = Reward.Type.EXTRA;
                    } else if(impact.equalsIgnoreCase("miss")) {
                        rwValue = 0;
                        rwType = Reward.Type.MISS;
                    }
                }
            }
            if(rwType == null) {
                System.out.println("Config Symbol.Reward.Type invalid: " + JsonUtils.serialize(entry));
                System.exit(4);
            }
            return SymbolBuilder.newSymbol(type, entry.getKey(), new Reward(rwType, rwValue));
        } catch(Exception ex) {
            System.out.println("Config Symbol invalid: " + JsonUtils.serialize(entry));
            System.exit(4);
        }
        return null;
    }
}
