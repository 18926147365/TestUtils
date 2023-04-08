package controller;

import bean.User;
import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.*;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.TestService;
import utils.CsvUtils;
import utils.OcrUtils;
import utils.RedisLuaUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author 李浩铭
 * @date 2020/7/9 17:00
 * @descroption
 */
@RestController
@RequestMapping("gs")
@Slf4j
public class GsController {

    static boolean run = true;
    @Autowired
    private RedisLuaUtils redisLuaUtils;

    @GetMapping("/start")
    public void start() throws Exception {

    }

}