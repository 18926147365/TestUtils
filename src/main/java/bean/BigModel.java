package bean;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/5/11 下午3:44
 */
public class BigModel {

    private String content;

    public BigModel(int k){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            builder.append("1111111111"+k);
        }
        this.content=builder.toString();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
