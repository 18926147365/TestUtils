package dingtalk;

import com.dingtalk.api.request.OapiRobotSendRequest;

/**
 * @author 李浩铭
 * @date 2020/8/17 9:48
 * @descroption
 */
public class DingText extends DingContentType {

    private String content;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DingText(){}
    public DingText(String content){
        this.content=content;
    }

    @Override
    public OapiRobotSendRequest content() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(getContent());
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
////        at.setAtMobiles(Arrays.asList("132xxxxxxxx"));
//// isAtAll类型如果不为Boolean，请升级至最新SDK
        at.setIsAtAll(true);
        request.setAt(at);
        return request;
    }

}
