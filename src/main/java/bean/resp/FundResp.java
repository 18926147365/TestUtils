package bean.resp;

import bean.Fund;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/6/11 上午10:35
 */
@Data
public class FundResp {

    private BigDecimal fundTotalAmount;//累计收益

    private BigDecimal todayEarAmount;//今天收益

    private BigDecimal todayEarGszzl;//今天涨跌幅度

    private String fundCode;

    @JSONField(name = "name")
    private String fundName;

    private Integer state;

    private BigDecimal gszzl;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date confirmTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date gztime;

    private BigDecimal payAmount;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date calcTime;

    private BigDecimal calcAmount;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date earTime;

    private BigDecimal earAmount;

    private String belongName;

    private String gztimeStr;



}
