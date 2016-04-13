/**
 *
 */
package com.niuhp.toolbox.excel;

import java.util.Comparator;

/**
 * Created by niuhp on 2016/4/13.
 */
public class CellInfoComparator implements Comparator<CellInfo> {

    @Override
    public int compare(CellInfo o1, CellInfo o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            Integer sheet1 = o1.getSheet();
            int sheet2 = o2.getSheet();
            if (sheet1 != sheet2) {
                return sheet1.compareTo(sheet2);
            }
            Integer row1 = o1.getRow();
            int row2 = o2.getRow();
            if (row1 != row2) {
                return row1.compareTo(row2);
            }
            Integer column1 = o1.getColumn();
            int column2 = o2.getColumn();
            return column1.compareTo(column2);
        }
    }

}
