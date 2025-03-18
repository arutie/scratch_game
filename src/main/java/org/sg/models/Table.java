package org.sg.models;

import java.util.ArrayList;
import java.util.List;

public class Table {
    public final int rows;
    public final int columns;
    private final List<List<Cell>> cells;

    public Table(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        cells = new ArrayList<>();
        for(int row=0; row<rows; ++row) {
            List<Cell> lsRow = new ArrayList<>();
            for(int col=0; col<columns; ++col) {
                lsRow.add(new Cell());
            }
            cells.add(lsRow);
        }
    }

    public Cell getCell(int row, int col) {
        List<Cell> lsRow = cells.get(row);
        if(lsRow == null || col >= lsRow.size()) {
            return null;
        }
        return lsRow.get(col);
    }

    public void randomSymbols() {
        for(List<Cell> lsCell : cells) {
            for(Cell cell : lsCell) {
                cell.randomSymbol();
            }
        }
    }
}
