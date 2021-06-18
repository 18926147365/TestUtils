package bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:12
 */
@Data
public class FundTalkConf {

    private Integer id;

    private String belongName ;

    private String belongId;

    private String accessToken;

    private Integer state;

    private BigDecimal amount;

    private Date createTime;
}
