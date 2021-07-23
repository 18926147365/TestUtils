package utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/7/8 下午3:19
 */
public class CodeValidUtil {

    public static void main(String[] args) {
        int[] des = {1,2};
        Integer[] departIds = Arrays.stream(des).boxed().toArray(Integer[]::new);
        System.out.println(departIds);
    }

    /**
     * @param code 编码
     * @return boolean
     * @Author lihaoming
     * @Description 验证备件及设备位置编码规则
     * @Date 2021/07/08
     **/
    public static boolean validCode(String code) {
        if (code == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[A-Z0-9]{6}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}$");
        Matcher matcher = pattern.matcher(code);
        return matcher.find();
    }
}
