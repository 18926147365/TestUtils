package bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/19 4:26 下午
 */
@Data
public class ModelResponse {
    private String brand;

    private Integer code;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String msg;


}
