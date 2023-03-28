package controller;

import bean.User;
import com.alibaba.fastjson.JSONObject;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
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

    @GetMapping("/start")
    public void start() throws Exception {
        while (run){
            Thread.sleep(3000);
            // 获取窗口句柄
            WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
            if (hwnd != null) {
                int len = User32.INSTANCE.GetWindowTextLength(hwnd);
                char[] chars = new char[len];
                User32.INSTANCE.GetWindowText(hwnd,chars,len+1);
                String title = String.valueOf(chars);
                // 获取窗口大小
                WinDef.RECT rect = new WinDef.RECT();
                User32.INSTANCE.GetWindowRect(hwnd, rect);
                int width = rect.right - rect.left;
                int height = rect.bottom - rect.top;


                // 获取窗口坐标
                WinDef.POINT point = new WinDef.POINT();
                User32.INSTANCE.GetCursorPos(point);
                System.out.println("窗口标题:"+title);
                System.out.println("窗口大小: " + width + "x" + height);
                System.out.println("left:"+ rect.left +"  right:"+rect.right +"  top:"+rect.top+"  bottom:"+ rect.bottom );
                System.out.println("鼠标当前位置: (" + point.x + ", " + point.y + ")");
                System.out.println();
                OcrUtils.capture(rect.left,rect.top,width,height);
            } else {
                System.out.println("找不到窗口");
            }
        }
    }




}