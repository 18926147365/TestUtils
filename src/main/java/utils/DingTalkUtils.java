package utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @Description:
 * @Author: lihaoming
 * @Date: 2021/3/16 下午4:18
 */
public class DingTalkUtils {

    /**
     * post 请求，发送给哪一个机器人
     *
     * @param reboot  机器人的token
     * @param message 发送的消息
     * @return
     */
    public static String sendPost(String reboot, String message) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(reboot);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");

        String textMsg = message;
        StringEntity se = new StringEntity(textMsg, "utf-8");
        httppost.setEntity(se);
        String result = null;
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
