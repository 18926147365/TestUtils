package bean;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/31 上午9:12
 */
@Data
public class FundTalkConf {

    private Integer id;

    private Integer fundId;

    private String accessToken;

    private Integer state;

    private Date createTime;
}
