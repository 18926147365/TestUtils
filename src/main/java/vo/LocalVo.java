package vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author lihaoming
 * @date 2023/3/30 18:05
 * @description
 */
@Data
public class LocalVo {
    static final double FBL = 1;


    private Integer x;
    private Integer y;
    private Integer height;
    private Integer width;

    private Integer fblx;
    private Integer fbly;
    private Integer fblHeight;
    private Integer fblWidth;

    public Integer getFblx() {
        return getFBL(x);
    }

    public Integer getFbly() {
        return getFBL(y);
    }

    public Integer getFblHeight() {
        return getFBL(height);
    }

    public Integer getFblWidth() {
        return getFBL(width);
    }

    static int getFBL(int v){
        return BigDecimal.valueOf(v).divide(BigDecimal.valueOf(FBL),2, RoundingMode.HALF_DOWN).intValue();
    }
}
