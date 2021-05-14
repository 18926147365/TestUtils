package service;

import bean.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;
import utils.DPUtils;

import java.util.Date;

/**
 * @author 李浩铭
 * @date 2020/7/15 15:58
 * @descroption
 */
@Service
public class TestService {

    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void test(User user){
       this.age=100;
        DPUtils.test(user);
    }
    public void test1(User user){

    }


    public String getName(){
        try {
            Thread.sleep(10000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("查询了");
        return "李先森"+new Date().getTime();
    }

}
