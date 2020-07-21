package controller;

import bean.User;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.TestService;
import system.LoggerProxy;
import utils.CsvUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 李浩铭
 * @date 2020/7/9 17:00
 * @descroption
 */
@RestController
@RequestMapping("test")
@Slf4j
public class TestController {
    private static String str = "123123";




    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestService testService;

    @RequestMapping("/report")
    public String report(HttpServletRequest request) throws Exception {


        String path = "D:/2.csv";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }


        //------------------------------------
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        List<User> list = userMapper.queryMoney();

        stopWatch.stop();
        System.out.println("查询时间花费：" + stopWatch.getTotalTimeSeconds() + "s");

        stopWatch = new StopWatch();
        stopWatch.start();
        Object[] header = {"id", "name", "money"};


        List<Object[]> dataList = new ArrayList<>();


        for (User user : list) {
            Object[] objects = new Object[3];
            objects[0] = user.getId();
            objects[1] = user.getName();
            objects[2] = user.getMoney().doubleValue();
            dataList.add(objects);
        }


        CsvUtils.writeCsv(header, dataList, path);

        stopWatch.stop();
        System.out.println("写入时间花费：" + stopWatch.getTotalTimeSeconds() + "s");

//            JavaMailUtils.Mail mail=new JavaMailUtils.Mail();
//            mail.setUser("18926147365@163.com");
//            mail.setPassword("GHBQMGRRMWUOKFUY");
//            mail.setContent("测试");
//            mail.setTitle("测试");
//            mail.setToAddree("18926147365@163.com");
//            mail.setFromAddress("18926147365@163.com");
//            mail.setFromName("李浩铭");
//            try {
//                JavaMailUtils.sendMail2(mail,new File("/1.csv"));
//            } catch (Exception e) {
//                e.printStackTrace();

        return "12";
    }


    @RequestMapping("/report2")
    public String report2(HttpServletRequest request) throws Exception {


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list1 = userMapper.queryMoneys(40000, 2000);
        stopWatch.stop();
        System.out.println("时间1花费：" + stopWatch.getTotalTimeSeconds() + "s");


        return "12";
    }

    @RequestMapping("/test")
    public String test(HttpServletRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list1 = userMapper.queryMoneys(1, 1);
        stopWatch.stop();
        System.out.println("时间2花费：" + stopWatch.getTotalTimeSeconds() + "s");
        return "123";
    }


    @RequestMapping("/test1")
    public String test1() {





        return "123";
    }

}
