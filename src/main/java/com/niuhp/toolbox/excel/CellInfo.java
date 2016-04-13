/**
 *
 */
package com.niuhp.toolbox.excel;

/**
 * Created by niuhp on 2016/4/13.
 */
public class CellInfo {

    private int sheet;
    private String sheetName;
    private int row;
    private int column;
    private int type;
    private Object value;

    public int getSheet() {
        return sheet;
    }

    public void setSheet(int sheet) {
        this.sheet = sheet;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CellInfo [sheet=" + sheet + ", sheetName=" + sheetName + ", row=" + row + ", column=" + column
                + ", type=" + type + ", value=" + value + "]";
    }

}
