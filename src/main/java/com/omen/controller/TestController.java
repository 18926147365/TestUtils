package com.omen.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李浩铭
 * @date 2020/7/9 17:00
 * @descroption
 */
@RestController
@RequestMapping("test")
@Slf4j
public class TestController {


    @RequestMapping("/testLog")
    public String testLog(){
           String str;

        try {
            int i=10/0;
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("测试123:{}","123",e);
        }
        return "123";
    }

    @RequestMapping("/testLog1")
    public String testLog1(){
        String str;
        try {
            int i=10/0;
        } catch (Exception e) {
            //e.printStackTrace();
            log.error("测试:{}","1234",e);
        }
        return "123";
    }


}
