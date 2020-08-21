package com.omen.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author 李浩铭
 * @date 2020/8/17 9:33
 * @descroption
 */
@Slf4j
public class DingTalkSend {


    private static final String DINGTALKURL="https://oapi.dingtalk.com/robot/send";

    private DingContentType dingContentType;

    private String accessToken;


    public DingContentType getDingContentType() {
        return dingContentType;
    }

    public void setDingContentType(DingContentType dingContentType) {
        this.dingContentType = dingContentType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public DingTalkSend(DingContentType dingContentType){
        this.dingContentType=dingContentType;
    }

    public DingTalkSend(String accessToken){
        this.accessToken=accessToken;
    }

    public OapiRobotSendResponse send(){

        DingTalkClient client = new DefaultDingTalkClient(DINGTALKURL+"?access_token="+accessToken);
        try {
            OapiRobotSendResponse response = client.execute(dingContentType.content());
            log.info("钉钉发送:"+response.getErrmsg());

            return response;
        } catch (ApiException e) {
            e.printStackTrace();
            //这里不能打错误日志log.error 否则出现递归循环的问题
        }
        OapiRobotSendResponse r=new OapiRobotSendResponse();
        r.setErrcode(-1l);
        r.setErrmsg("系统异常");
        return r;
    }

    public boolean sendSuccess(){
        OapiRobotSendResponse response= send();
        return  response.isSuccess();
    }


}
