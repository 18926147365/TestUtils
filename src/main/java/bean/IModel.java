package bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/10/14 11:47 上午
 */
@Data
public class IModel {

    private String brand;

    private String model;

    private Integer status;

    private  List<Map.Entry<String, Integer>> brands;


}
