package com.lemon.utils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.lemon.pojo.WriteBackData;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: java-auto-api
 * @Author: wss
 * @create: 2020-10-28 14:45
 * @Desc:
 */
public class ExcelUtils {
//    public static void main(String[] args) throws Exception {
//        List<CaseInfo> list = read(0, 1, CaseInfo.class);
//        for (CaseInfo caseInfo : list){
//            System.out.println(list);
//        }
//
//        List<API> list1 = read(1, 1, API.class);
//        for (API api :list1){
//            System.out.println(api);
//        }
//    }

    //批量回写存储List集合
    public static List<WriteBackData> wbdList = new ArrayList<>();

    /**
     * 读取excel数据并封装到指定对象中
     * @param sheetIndex  开始sheet索引
     * @param sheetNum    sheet个数
     * @param clazz       excel映射类字节对象
     * @return
     * @throws Exception
     */
    public static List read(int sheetIndex,int sheetNum,Class clazz) throws Exception {
        //1、excel文件流
        FileInputStream fis = new FileInputStream(Constants.EXCEL_PATH);
        //2、easypoi导入参数
        ImportParams params = new ImportParams();
        //从第0个sheet开始读取
        params.setStartSheetIndex(sheetIndex);
        //读取1个sheet
        params.setSheetNum(sheetNum);
        //3、导入 importExcel(execl文件流，映射关系字节码对象，导入参数)
        List caseInfoList = ExcelImportUtil.importExcel(fis, clazz, params);
        //4、关流
        fis.close();
        return caseInfoList;
    }

    /**
     * 批量回写，只需要执行一次
     * @throws Exception
     */
    public static void batchWriter() throws Exception {
        //回写逻辑：变量wbdList集合，取出sheetIndex、rowNum、cellNum、content
        FileInputStream fis = new FileInputStream(Constants.EXCEL_PATH);
        //获取所有的sheet
        Workbook sheets = WorkbookFactory.create(fis);
        for (WriteBackData wbd : wbdList){
            int sheetIndex = wbd.getSheetIndex();
            int rowNum = wbd.getRowNum();
            int cellNum = wbd.getCellNum();
            String content = wbd.getContent();
            //获取对象的sheet对象
            Sheet sheet = sheets.getSheetAt(sheetIndex);
            //获取对应的row对象
            Row row = sheet.getRow(rowNum);
            //获取对应的cell对象
            Cell cell = row.getCell(cellNum,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            //回写内容
            cell.setCellValue(content);
        }

        //回写到excel文件中
        FileOutputStream fos = new FileOutputStream(Constants.EXCEL_PATH);
        sheets.write(fos);
        fis.close();
        fos.close();
    }


}
