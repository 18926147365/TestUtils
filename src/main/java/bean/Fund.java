package bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gztime;

    private BigDecimal gszzl;

    private BigDecimal payAmount;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date calcTime;

    private BigDecimal calcAmount;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date earTime;

    private BigDecimal earAmount;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    private String belongName;


}
