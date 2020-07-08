
import bean.User;

import java.util.ArrayList;
import java.util.List;


/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */
public class Test {

    public static void main(String[] args) {
        List<String> list=new ArrayList<String>();
        list.add("123");
        if(list.contains("123")){
            list.remove("123");
        }
        System.out.println(list);
    }

    /**
     *1. 从剪切板获得文字。
     */

}
