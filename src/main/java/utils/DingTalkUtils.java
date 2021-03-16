package utils;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import dingtalk.DingMarkDown;
import dingtalk.DingTalkSend;
import dingtalk.DingText;

import java.util.Arrays;

/**
 * @author 李浩铭
 * @date 2020/8/17 8:55
 * @descroption
 */
public class DingTalkUtils {
    public static void main(String[] args) throws ApiException {
//        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=3fc73a1eba534bfe8cbbccc67b5d77dfbbb7ba752bbc7d5842d73149f5653952");
//        OapiRobotSendRequest request = new OapiRobotSendRequest();
//        request.setMsgtype("text");
//        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
//        text.setContent("测试文本消息");
//        request.setText(text);
//        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
////        at.setAtMobiles(Arrays.asList("132xxxxxxxx"));
//// isAtAll类型如果不为Boolean，请升级至最新SDK
//        at.setIsAtAll(true);
//        request.setAt(at);
//
//        request.setMsgtype("link");
//        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
//        link.setMessageUrl("https://www.dingtalk.com/");
//        link.setPicUrl("");
//        link.setTitle("时代的火车向前开");
//        link.setText("这个即将发布的新版本，创始人xx称它为红树林。而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是红树林");
//        request.setLink(link);
//
//        request.setMsgtype("markdown");
//        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
//        markdown.setTitle("杭州天气测试");
//        markdown.setText("#### 日志杭州天气 @156xxxx8827\n" +
//                "> 9度，西北风1级，空气良89，相对温度73%\n\n" +
//                "> ![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png)\n"  +
//                "> ###### 10点20分发布 [天气](http://www.thinkpage.cn/) \n");
//        request.setMarkdown(markdown);
//        OapiRobotSendResponse response = client.execute(request);
//        System.out.println(response.getErrcode());
//        System.out.println(response.getMsg());


//        DingMarkDown markDown = new DingMarkDown("测试日志收益", "侧是是是\n");
//        markDown.h1("收益");
//        markDown.setAtAll(false);
//        DingTalkSend dingTalkSend = new DingTalkSend(markDown);
//        dingTalkSend.setAccessToken("e13e4148cb80bb1927cd5d9e8f340590b7df06780587c0233c9fa9b996647a9a");
//        System.out.println(dingTalkSend.sendSuccess());

//        String downUpTip = "涨:" + upFundTotal + "支,跌:" + downFundTotal + "支,共收益:"+(int)(totalCalcMoney)+"元";
        String content = "休盘20210316\n涨:1支 跌:2支\n收益:+122元\n[-3.22%]:天弘创业板ETF联接基金C\n[+3.3%]:中欧医疗健康混合C\n";
        DingText dingText = new DingText(content);
        DingTalkSend dingTalkSend = new DingTalkSend(dingText);
        dingTalkSend.setAccessToken("e13e4148cb80bb1927cd5d9e8f340590b7df06780587c0233c9fa9b996647a9a");
        dingTalkSend.send();
    }
}
