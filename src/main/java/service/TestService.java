package service;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 李浩铭
 * @date 2020/7/15 15:58
 * @descroption
 */
@Service
public class TestService {


    public void test(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("test");
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
