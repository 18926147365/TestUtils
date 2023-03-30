package utils;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Location;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import vo.LocalVo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author lihaoming
 * @date 2023/3/30 17:46
 * @description
 */
public class SikuliUtils {

    public static final String PATH = "/Users/lihaoming/Desktop/";

    public static void main(String[] args) throws Exception {
        LocalVo localVo = WinDefUtils.getLocalVo("");
        waitBest(localVo,"123123");
    }

    public static Match waitBest(LocalVo vo,String fileName){
        Region region = Region.grow(new Location(vo.getX(), vo.getY()), vo.getWidth(), vo.getHeight());
        Match wait = region.waitBest(100,PATH + fileName);
        return wait;
    }

    public static Match exists(LocalVo vo,String fileName){
        Region region = Region.grow(new Location(vo.getX(), vo.getY()), vo.getWidth(), vo.getHeight());
        Match wait = region.exists(PATH + fileName);
        return wait;
    }


}
