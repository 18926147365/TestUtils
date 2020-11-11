package bean;

import javax.swing.*;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/11/6 3:19 下午
 */
public enum BoxReject {
    NONE("none"),//溢出不处理
    FIFO("fifo"),//先进先出
    KEEPALIVE("keepalive");//根据存活时长处理，存活时间短抛弃

    private String name;
    BoxReject(String name) {
        this.name = name;
    }
}
