package com.es7.demoes7.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;

import com.es7.demoes7.service.MSGService;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 验证码
 */
@Service
public class MSGServiceImpl implements MSGService {

    @Override
    public boolean send(Map map, String phone) {
        if(StringUtils.isEmpty(phone)) return false;

        //下面导包看着点，千万别导错了，对着上面我的导包
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId("LTAI5tHxy6XU7wDqfPuurTLb")
                // 您的AccessKey Secret	（这两个还不知道的去我前两次关于阿里云的有教程哪里找）
                .setAccessKeySecret("JlUUey3KtKCuqpSQqxyjwY4IOftSMj");
        // 访问的域名（这个不用变都是这个）
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = null;
        try {
            client = new Client(config);
            SendSmsRequest request = new SendSmsRequest();

            request.setSignName("阿里云短信测试");//签名名称
            request.setTemplateCode("SMS_154950909");//模版Code
            request.setPhoneNumbers("17742162921");//电话号码
            //这里的参数是json格式的字符串
            request.setTemplateParam(JSONObject.toJSONString(map));
            SendSmsResponse response = client.sendSms(request);
            System.out.println("发送成功："+new Gson().toJson(response));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


