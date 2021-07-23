package controller;

import bean.Model;
import mapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import utils.PhoneBrandUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/7/1 下午5:46
 */
@Component
public class BaseController {

    @Autowired
    private HttpServletRequest request;



    @Autowired
    private ModelMapper modelMapper;


    public String getBrand(){
        try {
            String ua =  getHeaderValue("user-agent");
            String model =  PhoneBrandUtils.getModel(ua);
            Model model1 = modelMapper.queryByUa(model);
            if(model1!=null){
                return model1.getBrand();
            }
        } catch (Exception e) {

        }
        return null;
    }
    public String getHeaderValue(String name){
        //获取所有请求头名称
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String v = headerNames.nextElement();
            //根据名称获取请求头的值
            if(v.equals(name)){
                String value = request.getHeader(name);
                return value;
            }
        }
        return null;
    }
}
