package easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/14 下午2:25
 */
@Data
public class DemoData {

    @ExcelProperty(index = 0)
    private Long id ;
    @ExcelProperty(index = 1)
    private String name;
    @ExcelProperty(index = 2)
    private BigDecimal money;


}
