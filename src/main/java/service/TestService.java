package service;

import org.springframework.stereotype.Service;

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

}
