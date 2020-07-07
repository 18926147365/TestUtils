package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CopyUtils;

import java.util.List;

/**
 * @author 李浩铭
 * @date 2020/7/7 17:39
 * @descroption
 */
@RestController
@RequestMapping("/copy")
public class CopyController {

    @RequestMapping("/getCopyList")
    public String getCopyList(){
        List<String> list=CopyUtils.cacheList;
        StringBuffer sBuffer=new StringBuffer();
        for (String s : list) {
            sBuffer.append(s+"<br/>");
        }
        return sBuffer.toString();
    }

}
