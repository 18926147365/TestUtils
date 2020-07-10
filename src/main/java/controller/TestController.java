package controller;

import bean.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author 李浩铭
 * @date 2020/7/9 17:00
 * @descroption
 */
@RestController
@RequestMapping("test")
public class TestController {
   private static String str="123123";



    @RequestMapping("/test")
    public String test(){
       synchronized (str.intern()){
           try {
               Thread.sleep(2000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }

        return "123";
    }


    @RequestMapping("/test1")
    public String test1(){
        synchronized (str.intern()){
            System.out.println("123123");
        }

        return "123";
    }

}
