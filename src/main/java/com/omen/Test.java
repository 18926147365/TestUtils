package com.omen;


import com.omen.bean.LogModel;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author 李浩铭
 * @date 2020/7/3 10:28
 * @descroption
 */

public class Test {

    public static void main(String[] args) {
        LogModel logModel=new LogModel();
        LogModel logModel1= ObjectUtils.clone(logModel);
        System.out.println((logModel == logModel1));

    }

}
