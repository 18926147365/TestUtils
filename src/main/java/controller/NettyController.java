package controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.NettyServerHandler;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/28 11:04 上午
 */
@RestController
@RequestMapping("netty")
@Slf4j
public class NettyController {

    @RequestMapping("/test")
    public String test(){
        NettyServerHandler.sendClientMsg();
        return  "1";
    }
}
