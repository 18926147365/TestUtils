package service;

import bean.Fund;
import mapper.FundMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/3/10 上午11:08
 */
@Service(value = "fundService")
public class FundService {

    @Autowired
    private FundMapper fundMapper;


    public List<Fund> queryAll(){
        return fundMapper.queryAll();
    }

    public void updateFund( String fundCode,BigDecimal gszzl, Date gztime){
        fundMapper.updateFund(fundCode, gszzl, gztime);
    }

}
