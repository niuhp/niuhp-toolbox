/**
 *
 */
package com.niuhp.toolbox.excel;

import com.niuhp.core.log.api.LogX;
import com.niuhp.core.logadapter.LogXManager;
import com.niuhp.core.util.IoUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * Created by niuhp on 2016/4/13.
 */
public class XlsxOperator {
    private static final LogX logx = LogXManager.getLogX(XlsxOperator.class);

    public List<CellInfo> readExcel(String excelPath) {

        List<CellInfo> cellInfos = new ArrayList<CellInfo>();
        InputStream is = null;

        try {
            File file = new File(excelPath);
            is = new FileInputStream(file);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            int sheetCount = xssfWorkbook.getNumberOfSheets();
            for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
                XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(sheetIndex);
                String sheetName = xssfSheet.getSheetName();
                int firstRowNum = xssfSheet.getFirstRowNum();
                int lastRowNum = xssfSheet.getLastRowNum();
                for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                    XSSFRow xssRow = xssfSheet.getRow(rowNum);
                    if (xssRow == null) {
                        continue;
                    }
                    int firstCellNum = xssRow.getFirstCellNum();
                    int lastCellNum = xssRow.getLastCellNum();
                    for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
                        CellInfo cellInfo = new CellInfo();
                        cellInfo.setSheet(sheetIndex);
                        cellInfo.setSheetName(sheetName);
                        cellInfo.setRow(rowNum);
                        cellInfo.setColumn(cellNum);
                        XSSFCell xssCell = xssRow.getCell(cellNum);
                        if (xssCell == null) {
                            continue;
                        }
                        int cellType = xssCell.getCellType();
                        Object value = getCellValue(cellType, xssCell);
                        cellInfo.setType(cellType);
                        cellInfo.setValue(value);
                        cellInfos.add(cellInfo);
                    }
                }
            }
        } catch (IOException e) {
            logx.error(String.format("read excel %s error", excelPath), e);
        } finally {
            IoUtil.close(is);
        }
        return cellInfos;
    }

    public void writeExcel(String excelPath, List<CellInfo> cellInfos) {
        Collections.sort(cellInfos, new CellInfoComparator());

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        fillXSSFWorkbook(xssfWorkbook, cellInfos);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(excelPath));
            xssfWorkbook.write(fos);
        } catch (IOException e) {
            logx.error(String.format("write excel %s error", excelPath), e);
        } finally {
            IoUtil.close(fos);
        }
    }

    public void fillXSSFWorkbook(XSSFWorkbook xssfWorkbook, List<CellInfo> cellInfos) {
        if (cellInfos == null || cellInfos.isEmpty()) {
            return;
        }
        autoCreateSheet(xssfWorkbook, cellInfos);
        for (CellInfo cellInfo : cellInfos) {
            if (cellInfo == null) {
                continue;
            }
            int sheetIndex = cellInfo.getSheet();
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(sheetIndex);
            int row = cellInfo.getRow();
            XSSFRow xssRow = xssfSheet.getRow(row);
            if (xssRow == null) {
                xssRow = xssfSheet.createRow(row);
            }
            int column = cellInfo.getColumn();
            int cellType = cellInfo.getType();
            XSSFCell xssfCell = xssRow.getCell(column);
            if (xssfCell == null) {
                xssfCell = xssRow.createCell(column, cellType);
            }

            Object value = cellInfo.getValue();
            setCellValue(cellType, xssfCell, value);
        }
    }

    private Object getCellValue(int cellType, XSSFCell xssCell) {
        switch (cellType) {
            case XSSFCell.CELL_TYPE_NUMERIC:
                return xssCell.getNumericCellValue();
            case XSSFCell.CELL_TYPE_STRING:
                return xssCell.getStringCellValue();
            case XSSFCell.CELL_TYPE_FORMULA:
                return xssCell.getCellFormula();
            case XSSFCell.CELL_TYPE_BOOLEAN:
                return xssCell.getBooleanCellValue();
            default:
                return "";
        }
    }

    private void setCellValue(int cellType, XSSFCell xssfCell, Object value) {
        if (value == null) {
            xssfCell.setCellValue("");
            return;
        }
        switch (cellType) {
            case XSSFCell.CELL_TYPE_NUMERIC:
                xssfCell.setCellValue((double) value);
                return;
            case XSSFCell.CELL_TYPE_STRING:
                xssfCell.setCellValue((String) value);
                return;
            case XSSFCell.CELL_TYPE_FORMULA:
                xssfCell.setCellValue((String) value);
                return;
            case XSSFCell.CELL_TYPE_BOOLEAN:
                xssfCell.setCellValue((boolean) value);
                return;
            default:
                xssfCell.setCellValue(value.toString());
                return;
        }
    }

    private void autoCreateSheet(XSSFWorkbook xssfWorkbook, List<CellInfo> cellInfos) {
        int maxSheetIndex = 0;
        Map<Integer, String> sheetNameMap = new HashMap<Integer, String>();

        for (CellInfo cellInfo : cellInfos) {
            int sheet = cellInfo.getSheet();
            if (sheet > maxSheetIndex) {
                maxSheetIndex = sheet;
            }
            String sheetName = cellInfo.getSheetName();
            if (!sheetNameMap.containsKey(sheet)) {
                sheetNameMap.put(sheet, sheetName);
            }
        }

        for (int sheetIndex = 0; sheetIndex <= maxSheetIndex; sheetIndex++) {
            String sheetName = sheetNameMap.get(sheetIndex);
            if (sheetName == null) {
                sheetName = String.format("sheet%s", sheetIndex);
            }
            xssfWorkbook.createSheet(sheetName);
        }
    }
}
