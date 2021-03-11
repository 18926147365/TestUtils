package bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/3/10 上午10:03
 */
public class Fund {
    private Integer id;

    private String fundCode;

    @JSONField(name = "name")
    private String fundName;

    private Integer state;

    private Date createTime;

    private Date gztime;

    private BigDecimal gszzl;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getGztime() {
        return gztime;
    }

    public void setGztime(Date gztime) {
        this.gztime = gztime;
    }

    public BigDecimal getGszzl() {
        return gszzl;
    }

    public void setGszzl(BigDecimal gszzl) {
        this.gszzl = gszzl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
