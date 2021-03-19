package dingtalk;

import com.dingtalk.api.request.OapiRobotSendRequest;

import java.util.Arrays;

/**
 * @author 李浩铭
 * @date 2020/8/17 18:11
 * @descroption
 */
public class DingMarkDown extends DingContentType {

    private String title;

    private StringBuffer text = new StringBuffer();

    private Boolean isAtAll;

    public Boolean getAtAll() {
        return isAtAll;
    }

    public void setAtAll(Boolean atAll) {
        isAtAll = atAll;
    }

    public DingMarkDown() {
    }

    public DingMarkDown(String title, String text) {
        this.text.append(text);
        this.title = title;
    }


    @Override
    public OapiRobotSendRequest content() {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        markdown.setText(text.toString());
        request.setMarkdown(markdown);
        return request;
    }

    public DingMarkDown add(String text) {
        this.text.append(text);
        return this;
    }

    public DingMarkDown add(String symbol, String content) {
        this.text.append(symbol + " ");
        this.text.append(content);
        return this;
    }

    public DingMarkDown h1(String content) {
        this.text.append("# ");
        this.text.append(content);
        this.text.append("\n\n");
        return this;
    }

    public DingMarkDown h2(String content) {
        this.text.append("## ");
        this.text.append(content);
        this.text.append("\n\n");
        return this;
    }

    public DingMarkDown h3(String content) {
        this.text.append("### ");
        this.text.append(content);
        this.text.append("\n\n");
        return this;
    }

    public DingMarkDown h4(String content) {
        this.text.append("#### ");
        this.text.append(content);
        this.text.append("\n\n");

        return this;
    }

    public DingMarkDown h5(String content) {
        this.text.append("##### ");
        this.text.append(content);
        this.text.append("\n\n");
        return this;
    }

    public DingMarkDown h6(String content) {
        this.text.append("###### ");
        this.text.append(content);
        this.text.append("\n\n");
        return this;
    }


    public DingMarkDown lineBreak() {
        this.text.append("\n\n");
        return this;
    }

    public DingMarkDown line(String title, String url) {
        this.text.append(String.format("[%s](%s)", title, url));
        return this;
    }

    public DingMarkDown img(String url) {
        this.text.append(String.format("![1](%s)", url));
        return this;
    }

    public DingMarkDown img(String url, String title) {
        this.text.append(String.format("![1](%s \"%s\") ", url, title));
        return this;
    }

    public DingMarkDown code(String content) {
        this.text.append(String.format("```\n\n%s```", content));
        return this;
    }


}
