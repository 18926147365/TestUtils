package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 李浩铭
 * @date 2020/7/8 10:40
 * @descroption
 */
@Controller
@RequestMapping("page")
public class IndexController {


    @RequestMapping("/index/copy")
    public String copyIndex(){
        return  "copy/index";
    }

    @RequestMapping("/index/json")
    public String jsonIndex(){
        return  "JSONUtils/index";
    }
}
