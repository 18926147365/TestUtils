package bean;

import lombok.Data;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/3/15 下午3:40
 */
@Data
public class NettyMsg {

    public static final String NOTIFY = "NOTIFY";
    private Integer code;

    private String msgType;

    private String msg;

}
