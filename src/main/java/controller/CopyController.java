package controller;

import bean.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CopyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李浩铭
 * @date 2020/7/7 17:39
 * @descroption
 */
@RestController
@RequestMapping("/copy")
public class CopyController {


    @RequestMapping("/index")
    public String test(){
        return "copy/index";
    }



    @RequestMapping("/getCopyList")
    public Result getCopyList(){
        List<String> list=CopyUtils.cacheList;

        Result result=new Result();
        result.setCode(0);
        result.setData(list);
        return result;
    }

    @RequestMapping("/clean")
    public String clean(){
       CopyUtils.cacheList=new ArrayList<>();
       return "success";
    }

    @RequestMapping("/getContent")
    public Result getContent(){
        Result result=new Result();
        result.setData(CopyUtils.getSysClipboardText());
        result.setCode(0);
        return result;
    }

}
