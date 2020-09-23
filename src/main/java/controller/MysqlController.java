package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/9/23 10:15 上午
 */

@RestController
@RequestMapping("mysql")
public class MysqlController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/test")
    public String test(){

//        DataSource dataSource=new
//        jdbcTemplate.setDataSource();
        String driverClassName="com.mysql.jdbc.Driver";
        String url="jdbc:mysql://42.194.205.61:8836/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";
        String user="root";
        String password="Aa972188163";
        try {
            DriverManagerDataSource driverManagerDataSource=new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName(driverClassName);
            driverManagerDataSource.setUrl(url);
            driverManagerDataSource.setUsername(user);
            driverManagerDataSource.setPassword(password);
            JdbcTemplate jdbcTemplate=new JdbcTemplate(driverManagerDataSource);
            List<Map<String, Object>> list= jdbcTemplate.queryForList("SELECT `SCHEMA_NAME`\n" +
                    "FROM `information_schema`.`SCHEMATA`");
            System.out.println(list);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }

        return "123";
    }
}
