package utils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * @author 李浩铭
 * @date 2020/7/7 17:50
 * @descroption
 */
@SpringBootApplication
@ComponentScan("controller")
public class Application {

    public static void main(String[] args) {

        System.setProperty("java.awt.headless", "false");
       SpringApplication.run(Application.class,args);
       CopyUtils.listenerCopy();
    }
}
