package bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/3/10 上午10:03
 */
@Data
public class Fund {
    private Integer id;

    private String fundCode;

    @JSONField(name = "name")
    private String fundName;

    private Integer state;

    private Date createTime;

    private Date gztime;

    private BigDecimal gszzl;

    private BigDecimal payAmount;

    private Date calcTime;

    private BigDecimal calcAmount;

    private Date earTime;

    private BigDecimal earAmount;

    private Date payTime;

    private String belongName;


}
