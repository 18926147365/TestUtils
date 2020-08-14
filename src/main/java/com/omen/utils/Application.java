package com.omen.utils;

import com.omen.proxy.LogProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 李浩铭
 * @date 2020/7/7 17:50
 * @descroption
 */
@SpringBootApplication
@ComponentScan({"com.omen.controller", "com.omen.utils"})
public class Application {

    static {
        new LogProxy();
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(Application.class, args);
    }

}
