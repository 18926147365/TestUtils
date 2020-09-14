package bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李浩铭
 * @date 2020/7/3 11:05
 * @descroption
 */
public class User {

    public User(){}

    public User(Integer id,String name,double money){
        this.id=id;
        this.name=name;
        this.money=BigDecimal.valueOf(money);
    }
    private Integer id;

    private Long businessId;

    private String name;

    private BigDecimal money;


    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
