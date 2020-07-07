import bean.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */
public class Test {

    public static void main(String[] args) {
        Integer a1=new Integer(5);
        Integer a2=new Integer(5);

        System.out.println(isEquals(null,null));
        System.out.println(isEquals(null,1));
        System.out.println(isEquals(2,null));
        System.out.println(isEquals(2,3));
        System.out.println(isEquals(2,2));
        System.out.println(isEquals(a1,a2));

    }

    private static boolean isEquals(Integer a1,Integer a2){
        if(a1==null && a2==null){
            return true;
        }
        if((a1==null && a2!=null) || (a1!=null && a2==null)){
            return false;
        }
        if(a1.intValue()==a2.intValue()){
            return true;
        }else {
            return  false;
        }
    }


    private static  List<User> getList(){
        User user=new User();
        user.setName("123");
        user.setId(1);

        User user1=new User();
        user1.setName("123");
        user1.setId(1);
        return Arrays.asList(user,user1);
    }
}
