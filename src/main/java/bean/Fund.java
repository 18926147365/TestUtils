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

    private BigDecimal payAmount;

    private Date calcTime;

    private BigDecimal calcAmount;

    private Date payTime;


    public BigDecimal getCalcAmount() {
        return calcAmount;
    }

    public void setCalcAmount(BigDecimal calcAmount) {
        this.calcAmount = calcAmount;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Date getCalcTime() {
        return calcTime;
    }

    public void setCalcTime(Date calcTime) {
        this.calcTime = calcTime;
    }

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
