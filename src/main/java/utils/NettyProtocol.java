package utils;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2020/11/11 9:57 上午
 */
public class NettyProtocol {
    private int length;

    private byte[] content;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
