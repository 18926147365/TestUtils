package utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 李浩铭
 * @date 2020/7/14 14:53
 * @descroption
 */
public class CsvUtils {
    /**
     * 写入csv文件
     * @param headers  列头
     * @param data     数据内容
     * @param filePath 创建的csv文件路径
     **/
    public static void writeCsv(Object[] headers, List<Object[]> data, String filePath) throws IOException {
        //初始化csvformat
        CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator("\n");
        //创建FileWriter对象
        FileWriter fileWriter = new FileWriter(new File(filePath), true);
        //创建CSVPrinter对象
        CSVPrinter printer = new CSVPrinter(fileWriter, formator);
        //写入列头数据
        printer.printRecord(headers);
        if (null != data) {
            //循环写入数据
            for (Object[] lineData : data) {
                printer.printRecord(lineData);
            }
        }
        fileWriter.flush();
        printer.close(true);
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        Object[] headers={"id","name"};
        List<Object[]> data=new ArrayList<>();
        Object[] data1={1,2};
        data.add(data1);
        writeCsv(headers,data,"D://1.csv");

    }


}
