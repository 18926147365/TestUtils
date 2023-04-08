package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import service.GsRedisService;

/**
 * @author lihaoming
 * @date 2023/4/8 16:44
 * @description
 */
@Controller
@RequestMapping("gs")
public class GsController {

    @Autowired
    private GsRedisService redisService;
    @RequestMapping("/index")
    public String index(){
        return  "gs/index";
    }
}
