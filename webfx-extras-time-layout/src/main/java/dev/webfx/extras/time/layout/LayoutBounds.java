package dev.webfx.extras.time.layout;

import dev.webfx.extras.geometry.MutableBounds;

/**
 * @author Bruno Salmon
 */
public final class LayoutBounds extends MutableBounds {

    private boolean valid;
    private int rowIndex, columnIndex; // redundant with cell (to be removed later)

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

}