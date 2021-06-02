package utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import easyexcel.DemoData;
import easyexcel.DemoDataListener;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/26 下午2:04
 */
public class EasyExcelUtils {
    public void read(){
        String path = "/Users/lihaoming/Desktop/demo.xlsx";
        EasyExcel.read(path, DemoData.class, new DemoDataListener()).sheet().doRead();
    }

    public static void write(){
        String path = "/Users/lihaoming/Desktop/demo.xlsx";
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData demoData = new DemoData();
            demoData.setId(Long.valueOf(i));
            demoData.setMoney(new BigDecimal("2"));
            demoData.setName("哈擦安全"+i);
            list.add(demoData);
        }
        EasyExcel.write(path).sheet().doWrite(list);
        EasyExcel.write(path).sheet().doWrite(list);
    }

    /**
     * 批量重复写
     * */
    public static void batchWrite(){
        String path = "/Users/lihaoming/Desktop/demo.xlsx";
        // 这里 需要指定写用哪个class去写
        ExcelWriter excelWriter = EasyExcel.write(path, DemoData.class).build();
        // 这里注意 如果同一个sheet只要创建一次
        WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
        // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData demoData = new DemoData();
            demoData.setId(Long.valueOf(i));
            demoData.setMoney(new BigDecimal("2"));
            demoData.setName("哈擦安全"+i);
            list.add(demoData);
        }
        // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
        for (int i = 0; i < 5; i++) {
            // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
            excelWriter.write(list, writeSheet);
        }
        excelWriter.finish();
        // 千万别忘记finish 会帮忙关闭流
    }
    public static void main(String[] args) {
        batchWrite();
    }
}
