/**
 *
 */
package com.niuhp.toolbox.excel;

import com.niuhp.core.util.IoUtil;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by niuhp on 2016/4/13.
 */
public class XlsOperator {
  private static final Logger logger = Logger.getLogger(XlsOperator.class);

  public List<CellInfo> readExcel(String excelPath) {

    List<CellInfo> cellInfos = new ArrayList<CellInfo>();
    InputStream is = null;

    try {
      File file = new File(excelPath);
      is = new FileInputStream(file);
      POIFSFileSystem poifsFileSystem = new POIFSFileSystem(is);

      HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
      int sheetCount = hssfWorkbook.getNumberOfSheets();
      for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetIndex);
        String sheetName = hssfSheet.getSheetName();
        int firstRowNum = hssfSheet.getFirstRowNum();
        int lastRowNum = hssfSheet.getLastRowNum();
        for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
          HSSFRow hssRow = hssfSheet.getRow(rowNum);
          if (hssRow == null) {
            continue;
          }
          int firstCellNum = hssRow.getFirstCellNum();
          int lastCellNum = hssRow.getLastCellNum();
          for (int cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) {
            CellInfo cellInfo = new CellInfo();
            cellInfo.setSheet(sheetIndex);
            cellInfo.setSheetName(sheetName);
            cellInfo.setRow(rowNum);
            cellInfo.setColumn(cellNum);
            HSSFCell hssCell = hssRow.getCell(cellNum);
            if (hssCell == null) {
              continue;
            }
            int cellType = hssCell.getCellType();
            Object value = getCellValue(cellType, hssCell);
            cellInfo.setType(cellType);
            cellInfo.setValue(value);
            cellInfos.add(cellInfo);
          }
        }
      }
    } catch (IOException e) {
      logger.error(String.format("read excel %s error", excelPath), e);
    } finally {
      IoUtil.close(is);
    }
    return cellInfos;
  }

  public void writeExcel(String excelPath, List<CellInfo> cellInfos) {
    Collections.sort(cellInfos, new CellInfoComparator());

    HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
    fillHSSFWorkbook(hssfWorkbook, cellInfos);

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(new File(excelPath));
      hssfWorkbook.write(fos);
    } catch (IOException e) {
      logger.error(String.format("write excel %s error", excelPath), e);
    } finally {
      IoUtil.close(fos);
    }
  }

  public void fillHSSFWorkbook(HSSFWorkbook hssfWorkbook, List<CellInfo> cellInfos) {
    if (cellInfos == null || cellInfos.isEmpty()) {
      return;
    }

    autoCreateSheet(hssfWorkbook, cellInfos);

    for (CellInfo cellInfo : cellInfos) {
      if (cellInfo == null) {
        continue;
      }
      int sheetIndex = cellInfo.getSheet();
      HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetIndex);
      int row = cellInfo.getRow();
      HSSFRow hssRow = hssfSheet.getRow(row);
      if (hssRow == null) {
        hssRow = hssfSheet.createRow(row);
      }
      int cellType = cellInfo.getType();
      int column = cellInfo.getColumn();
      HSSFCell hssfCell = hssRow.getCell(column);
      if (hssfCell == null) {
        hssfCell = hssRow.createCell(column, cellType);
      }
      Object value = cellInfo.getValue();
      setCellValue(cellType, hssfCell, value);
    }

  }

  private Object getCellValue(int cellType, HSSFCell hssCell) {
    switch (cellType) {
      case HSSFCell.CELL_TYPE_NUMERIC:
        return hssCell.getNumericCellValue();
      case HSSFCell.CELL_TYPE_STRING:
        return hssCell.getStringCellValue();
      case HSSFCell.CELL_TYPE_FORMULA:
        return hssCell.getCellFormula();
      case HSSFCell.CELL_TYPE_BOOLEAN:
        return hssCell.getBooleanCellValue();
      default:
        return "";
    }
  }

  private void setCellValue(int cellType, HSSFCell hssfCell, Object value) {
    if (value == null) {
      hssfCell.setCellValue("");
      return;
    }
    switch (cellType) {
      case HSSFCell.CELL_TYPE_NUMERIC:
        hssfCell.setCellValue((double) value);
        return;
      case HSSFCell.CELL_TYPE_STRING:
        hssfCell.setCellValue((String) value);
        return;
      case HSSFCell.CELL_TYPE_FORMULA:
        hssfCell.setCellValue((String) value);
        return;
      case HSSFCell.CELL_TYPE_BOOLEAN:
        hssfCell.setCellValue((boolean) value);
        return;
      default:
        hssfCell.setCellValue(value.toString());
        return;
    }
  }

  private void autoCreateSheet(HSSFWorkbook hssfWorkbook, List<CellInfo> cellInfos) {
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
      hssfWorkbook.createSheet(sheetName);
    }
  }
}
