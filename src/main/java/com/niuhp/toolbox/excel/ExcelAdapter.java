/**
 *
 */
package com.niuhp.toolbox.excel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * Created by niuhp on 2016/4/13.
 */
public class ExcelAdapter {

  private static final Logger logx = Logger.getLogger(ExcelAdapter.class);
  private XlsOperator xlsOperator = new XlsOperator();
  private XlsxOperator xlsxOperator = new XlsxOperator();

  public List<CellInfo> readExcel(String excelPath) {
    if (excelPath == null || excelPath.trim().isEmpty()) {
      logx.warn(String.format("excelPath is empty"));
      return null;
    }
    excelPath = excelPath.toLowerCase();
    if (excelPath.endsWith("xlsx")) {
      return xlsxOperator.readExcel(excelPath);
    } else if (excelPath.endsWith("xls")) {
      return xlsOperator.readExcel(excelPath);
    } else {
      logx.warn(String.format("unsupport excelPath:%s", excelPath));
      return null;
    }
  }

  public void writeExcel(String excelPath, List<CellInfo> cellInfos) {
    if (excelPath == null || excelPath.trim().isEmpty()) {
      logx.warn(String.format("excelPath is empty"));
      return;
    }
    excelPath = excelPath.toLowerCase();
    if (excelPath.endsWith("xlsx")) {
      xlsxOperator.writeExcel(excelPath, cellInfos);
    } else if (excelPath.endsWith("xls")) {
      xlsOperator.writeExcel(excelPath, cellInfos);
    } else {
      logx.warn(String.format("unsupport excelPath:%s", excelPath));
    }
  }

  public void fillXSSFWorkbook(XSSFWorkbook xssfWorkbook, List<CellInfo> cellInfos) {
    xlsxOperator.fillXSSFWorkbook(xssfWorkbook, cellInfos);
  }

  public void fillHSSFWorkbook(HSSFWorkbook hssfWorkbook, List<CellInfo> cellInfos) {
    xlsOperator.fillHSSFWorkbook(hssfWorkbook, cellInfos);
  }
}
