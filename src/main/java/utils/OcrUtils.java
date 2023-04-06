package utils;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import okhttp3.*;
import org.sikuli.script.ScreenImage;
import vo.LocalVo;

import java.io.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @author lihaoming
 * @date 2023/3/28 11:50
 * @description
 */
public class OcrUtils {


    static String ACCESS_TOKEN = "24.62d7ae480303bcb87ac1fb3da3c93038.2592000.1683382703.282335-32076196";

    static Long lastDate = null;
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();


    public static void main(String []args) throws Exception{
        System.out.println(getNameAndLevel());
    }

    public static String getNameAndLevel() throws Exception{
        SikuliUtils.openJs();
        LocalVo localVo = WinDefUtils.getLocalVo();
        int x = localVo.getFblx() + 203;
        int y = localVo.getFbly() + 167;
        ScreenImage capture = SikuliUtils.screen.capture(x, y, 246, 38);
        List<String> baiduText = getBaiduText(capture.getFile());
        if(CollUtil.isNotEmpty(baiduText)){
            return baiduText.get(0).replace(",","");
        }
        return "";
    }

    public static String getMoney() throws Exception{
        SikuliUtils.openBB();
        LocalVo localVo = WinDefUtils.getLocalVo();
        int x = localVo.getFblx() + 124;
        int y = localVo.getFbly() + 662;
        ScreenImage capture = SikuliUtils.screen.capture(x, y, 114, 30);
        List<String> baiduText = getBaiduText(capture.getFile());
        if(CollUtil.isNotEmpty(baiduText)){
            return baiduText.get(0).replace(",","");
        }
        return "";
    }

    public static List<String> getBaiduText(String fileName){
        List<String> result = null;
        try {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String fileContentAsBase64 = getFileContentAsBase64Urlencoded(fileName);
            fileContentAsBase64= "image="+fileContentAsBase64;
            RequestBody body = RequestBody.create(mediaType,fileContentAsBase64 );
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token="+getBaiduAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            //words_result
            JSONObject re = JSONObject.parseObject(response.body().string());
            System.out.println(re);
            List<String> wordsResult = Convert.toList(String.class, re.getJSONArray("words_result"));
            result = new ArrayList<>();
            for (String s : wordsResult) {
                JSONObject j = JSONObject.parseObject(s);
                result.add(j.getString("words"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }


    public static String getBaiduAccessToken() throws Exception {
        if (lastDate == null || new Date().getTime() > lastDate) {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/oauth/2.0/token?client_id=Mq9w2lGMvDeEGTT30766FhrR&client_secret=MHbOF7comruspL43ZF2Cq7Q0coybxj6v&grant_type=client_credentials")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            JSONObject data = JSONObject.parseObject(response.body().string());
            String accessToken = data.getString("access_token");
            DateTime dateTime = DateUtil.offsetHour(new Date(), 12);
            lastDate = dateTime.getTime();
            ACCESS_TOKEN = new String(accessToken);
            return accessToken;
        } else {
            return ACCESS_TOKEN;
        }

    }


    public static String getText(String name) throws Exception {

        BufferedImage image = ImageIO.read(new File(SikuliUtils.PATH + name + ".png"));

        return doOCR(image);
    }

    public static String doOCR(BufferedImage image) throws TesseractException {
        //创建Tesseract对象
        ITesseract tesseract = new Tesseract();
        //设置中文字体库路径
        tesseract.setDatapath("D:\\git_work\\TestUtils\\src\\main\\resources\\tessdata");
        //中文识别
        tesseract.setLanguage("chi_sim");
        //执行ocr识别
        String result = tesseract.doOCR(image);
        //替换回车和tal键  使结果为一行
//        result = result.replaceAll("\\r|\\n", "-").replaceAll(" ", "");
        return result;
    }

    /**
     * 获取文件base64编码
     * @param path 文件路径
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    static String getFileContentAsBase64(String path) throws IOException {
        byte[] b = Files.readAllBytes(Paths.get(path));
        return Base64.getEncoder().encodeToString(b);
    }
    /**
     * 获取文件base64 UrlEncode编码
     * @param path 文件路径
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    static String getFileContentAsBase64Urlencoded(String path) throws IOException {
        return URLEncoder.encode(getFileContentAsBase64(path), "utf-8");
    }



}
