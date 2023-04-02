package vo;

import lombok.Data;
import org.sikuli.script.Match;

@Data
public class MatchVo {

    private Match match;

    //c次数
    private int cycleIndex;
}
