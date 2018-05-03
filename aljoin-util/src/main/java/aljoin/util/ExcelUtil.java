package aljoin.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtil {

    private final static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 将数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param title 表格标题名
     * @param headers 表格属性列名二维数组{英文列名，中文列头} String headers[][] =
     *        {{"NAME","名称"},{"ACCOUNT","账号"},{"TRANAMT","金额"},{"REPORTDATE","统计日期"}};
     * @param dataset List<Map>集合数据
     * @param response 与输出设备关联的流对象，可以将EXCEL文档导出到本地或者网络中
     */
    public static void exportExcel(String title, String[][] headers, List<Map<String, Object>> dataset,
        HttpServletResponse response) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(15);
        HSSFCellStyle style = createHeadStyle(workbook);// 创建标题行样式
        // HSSFCellStyle style2 = createCommonDataStyle(workbook);// 创建单元格样式
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i][1]);
            cell.setCellValue(text);
        }
        HSSFFont font3 = workbook.createFont();
        String textValue = null;
        if (null != dataset) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < dataset.size(); i++) {
                map = dataset.get(i);
                int index = 0;
                row = sheet.createRow(i + 1);
                for (int j = 0; j < headers.length; j++) {
                    textValue = map.get(headers[j][0]) == null ? "" : map.get(headers[j][0]).toString();
                    if (null != textValue) {
                        HSSFCell cell = row.createCell(index++);
                        cell.setCellStyle(style);
                        HSSFRichTextString richString = new HSSFRichTextString(textValue);

                        // font3.setColor(HSSFColor.BLUE.index);
                        richString.applyFont(font3);
                        cell.setCellValue(richString);
                    }
                }
            }
        }

        try {

            response.reset();
            response.setContentType("application/vnd..ms-excel");
            response.setHeader("content-Disposition",
                "attachment;filename=" + URLEncoder.encode(title + ".xls", "utf-8"));
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            logger.error("", e);
        }

    }

    /**
     * @Title: exportExcel
     * @Description: 导出Excel的方法
     * @author: evan @ 2017-05-02
     * @param workbook
     * @param sheetNum (sheet的位置，0表示第一个表格中的第一个sheet)
     * @param sheetTitle （sheet的名称）
     * @param headers （表格的标题）
     * @param dataset （表格的数据）
     * @param response （输出流）
     * @throws Exception
     */
    public static void exportExcel(HSSFWorkbook workbook, int sheetNum, String sheetTitle, String cellTitle,
        String[][] headers, List<Map<String, Object>> dataset, HttpServletResponse response) throws Exception {
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet();
        if (null != sheetTitle && sheetTitle.length() > 0) {
            String regEx = "[~!/@#$%^&*()-_=+\\|[{}];:\'\",<.>/?]+";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(sheetTitle);
            if (m.find()) {
                sheetTitle = regexReplace(regEx, sheetTitle);
            }
            workbook.setSheetName(sheetNum, sheetTitle);
        }
        // 设置表格默认列宽度为20个字节
        sheet.setDefaultColumnWidth((int)20);
        HSSFCellStyle style = createHeadStyle(workbook);// 创建标题行样式
        // 产生表格标题行
        HSSFRow row = sheet.createRow((int)0);
        HSSFCell cell = null;
        for (int i = 0; i < headers.length; i++) {
            cell = row.createCell((int)i);
            cell.setCellValue(cellTitle);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            cell.setCellStyle(style);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length));

        // 产生表头
        HSSFRow row2 = sheet.createRow((int)1);
        HSSFCell cell2 = null;
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        for (int i = 0; i < headers.length; i++) {
            cell2 = row2.createCell((int)i);
            HSSFRichTextString header = new HSSFRichTextString(headers[i][1]);
            cell2.setCellValue(header);
            cell2.setCellStyle(style2);
        }

        // 遍历集合数据，产生数据行
        String textValue = null;
        if (null != dataset) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < dataset.size(); i++) {
                map = dataset.get(i);
                int index = 0;// 表头所在的位置
                row = sheet.createRow(i + 2);
                row.setHeightInPoints(20);
                for (int j = 0; j < headers.length; j++) {
                    textValue = map.get(headers[j][0]) == null ? "" : map.get(headers[j][0]).toString();
                    if (null != textValue) {
                        HSSFCell cell0 = row.createCell(index++);
                        HSSFRichTextString richString = new HSSFRichTextString(textValue);
                        HSSFFont font3 = workbook.createFont();
                        // font3.setColor(HSSFColor.BLUE.index);
                        richString.applyFont(font3);
                        cell0.setCellValue(richString);
                    }
                }
            }
        }
        /*
         * try { //response.reset(); response.setContentType("application/msexcel");
         * response.setHeader("content-Disposition","attachment;filename="+URLEncoder.encode(
         * workBookTitle + ".xls","utf-8")); OutputStream out = response.getOutputStream();
         * workbook.write(out); out.close(); response.flushBuffer(); } catch (IOException e) {
         * logger.error("",e); }
         */
    }

    /**
     * 利用递归找一个类的指定方法，如果找不到，去父亲里面找直到最上层Object对象为止。
     *
     * @param clazz 目标类
     * @param methodName 方法名
     * @param classes 方法参数类型数组
     * @return 方法对象
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Method getMethod(Class clazz, String methodName, final Class[] classes) throws Exception {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName, classes);
                }
            }
        }
        return method;
    }

    /**
     *
     * @param title sheet标题
     * @param headers 表格 头部
     * @param dataset 数据集
     * @param response 响应对象
     * @param startRow 开始行
     * @param endRow 结束行
     * @param mergeCells 需要合并列的列号
     */
    public static void exportExcel(String title, String[][] headers, List<Map<String, Object>> dataset,
        HttpServletResponse response, Integer[] startRow, Integer[] endRow, Integer[] mergeCells) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(15);
        HSSFCellStyle style = createHeadStyle(workbook);// 创建标题行样式
        // HSSFCellStyle style2 = createCommonDataStyle(workbook);//创建单元格样式
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i][1]);
            cell.setCellValue(text);
        }
        HSSFFont font3 = workbook.createFont();
        String textValue = null;
        if (null != dataset) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < dataset.size(); i++) {
                map = dataset.get(i);
                int index = 0;
                row = sheet.createRow(i + 1);
                for (int j = 0; j < headers.length; j++) {
                    HSSFCell cell = row.createCell(index++);
                    cell.setCellStyle(style);
                    textValue = map.get(headers[j][0]) == null ? "" : map.get(headers[j][0]).toString();
                    if (null != textValue) {
                        HSSFRichTextString richString = new HSSFRichTextString(textValue);
                        richString.applyFont(font3);
                        cell.setCellValue(richString);
                    }
                }
            }
            if (null != mergeCells) {
                for (int j = 0; j <= mergeCells.length - 1; j++) {
                    if (null != endRow && null != startRow && endRow.length > 0 && startRow.length > 0) {
                        for (int i = 0; i < endRow.length; i++) {
                            // 起始行号，终止行号， 起始列号，终止列号
                            // startRow: 1 2 endRow 2 8
                            sheet.addMergedRegion(
                                new CellRangeAddress(startRow[i], endRow[i], mergeCells[j], mergeCells[j]));
                        }
                    }
                }
            }
        }

        try {
            response.reset();
            response.setContentType("application/vnd..ms-excel");
            response.setHeader("content-Disposition",
                "attachment;filename=" + URLEncoder.encode(title + ".xls", "utf-8"));
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    /**
     * 创建合计行
     * 
     * @param sheet
     * @param row
     * @param headers
     * @param sumCells
     * @param sumRowStyle
     * @param numStyle
     */
    @SuppressWarnings("unused")
    private static void createSumRow(HSSFSheet sheet, HSSFRow row, final String[][] headers, final Integer[] sumCells,
        HSSFCellStyle sumRowStyle, HSSFCellStyle numStyle) {
        row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(sumRowStyle);
        }
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            for (int j = 0; j < sumCells.length; j++) {
                sheet.getRow(i).getCell(sumCells[j])
                    .setCellValue(Double.parseDouble(sheet.getRow(i).getCell(sumCells[j]).getStringCellValue()));
                sheet.getRow(i).getCell(sumCells[j]).setCellStyle(numStyle);
            }
        }
        HSSFCell sumCell = row.getCell(0);
        sumCell.setCellValue("合计：");
        String sumFunctionStr = null;
        for (int i = 0; i < sumCells.length; i++) {
            sumFunctionStr = "SUM(" + CellReference.convertNumToColString(sumCells[i]) + "2:"
                + CellReference.convertNumToColString(sumCells[i]) + sheet.getLastRowNum() + ")";
            row.getCell(sumCells[i]).setCellFormula(sumFunctionStr);
        }
    }

    /**
     * 标题单元格样式
     * 
     * @param workbook
     * @return
     */
    private static HSSFCellStyle createHeadStyle(HSSFWorkbook workbook) {
        // 标题单元格样式
        HSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 标题单元格字体
        HSSFFont headFont = workbook.createFont();
        headFont.setColor(HSSFColor.BLACK.index);
        headFont.setFontHeightInPoints((short)12);
        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        headStyle.setFont(headFont);
        // 指定当单元格内容显示不下时自动换行
        headStyle.setWrapText(true);
        return headStyle;
    }

    /**
     * 合计行单元格样式
     * 
     * @param workbook
     * @return
     */
    @SuppressWarnings("unused")
    private static HSSFCellStyle createSumRowStyle(HSSFWorkbook workbook) {
        // 合计行单元格样式
        HSSFCellStyle sumRowStyle = workbook.createCellStyle();
        sumRowStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        sumRowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sumRowStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        sumRowStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        sumRowStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        sumRowStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        sumRowStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        sumRowStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        // 合计行单元格字体
        HSSFFont sumRowFont = workbook.createFont();
        sumRowFont.setColor(HSSFColor.BLACK.index);
        sumRowFont.setFontHeightInPoints((short)12);
        sumRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        sumRowStyle.setFont(sumRowFont);
        return sumRowStyle;
    }

    /**
     * 自定义保留两位小数数字单元格格式
     * 
     * @param workbook
     * @return
     */
    @SuppressWarnings("unused")
    private static HSSFCellStyle createNumStyle(HSSFWorkbook workbook) {
        // 自定义保留两位小数数字单元格格式
        HSSFCellStyle numStyle = workbook.createCellStyle();
        numStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        numStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        numStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        numStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        numStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        numStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        numStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        numStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        numStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        // 自定义保留两位小数数字单元格字体
        HSSFFont numFont = workbook.createFont();
        numFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        numStyle.setFont(numFont);
        return numStyle;
    }

    /**
     * 检测并替换字符串中特殊字符为空格
     * 
     * @param str 字符串
     * @return
     */
    public static String regexReplace(String regEx, String str) {
        Pattern p = null;
        Matcher m = null;
        String value = null;

        p = Pattern.compile(regEx);
        m = p.matcher(str);
        String temp = str;
        // 下面的while循环式进行循环匹配替换，把找到的所有
        // 符合匹配规则的字串都替换为你想替换的内容
        while (m.find()) {
            value = m.group(0);
            temp = temp.replace(value, " ");
        }
        return temp;
    }
}
