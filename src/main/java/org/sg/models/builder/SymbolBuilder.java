package org.sg.models.builder;

import org.sg.models.Reward;
import org.sg.models.Symbol;
import org.sg.models.symbol.BonusSymbol;
import org.sg.models.symbol.StandardSymbol;

public class SymbolBuilder {
    public static Symbol newSymbol(Symbol.Type type, String name, Reward reward) {
        switch (type) {
            case STANDARD -> {
                return newStandardSymbol(name, reward);
            }
            case BONUS -> {
                return newBonusSymbol(name, reward);
            }
        }
        return null;
    }
    public static Symbol newStandardSymbol(String name, Reward reward) {
        return new StandardSymbol(name, reward);
    }
    public static Symbol newBonusSymbol(String name, Reward reward) {
        return new BonusSymbol(name, reward);
    }
}
