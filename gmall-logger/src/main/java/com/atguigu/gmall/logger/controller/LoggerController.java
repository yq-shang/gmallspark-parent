package com.atguigu.gmall.logger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.atguigu.gmall.comman.*;

@RestController  //Controller+Responsebody
@Slf4j
public class LoggerController {

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;//kv类型的logString=log json字符串


    @PostMapping("log")//参数不能随便写，"http://logserver/log"，要把log下的日志传入这个方法
    public String testLoger(@RequestParam("logString") String logString){
        //加时间戳
        JSONObject jsonObject = JSON.parseObject(logString);
        jsonObject.put("ts",System.currentTimeMillis());

        //落盘成文件
        String jsonString = jsonObject.toJSONString();
        //注意这里直接使用log会报错，需要在idea中安装lombok插件
        log.info(jsonString);

        //推送到kafka
        if( "startup".equals( jsonObject.getString("type"))){
            kafkaTemplate.send(GmallConstants.KAFKA_TOPIC_STARTUP,jsonString);
        }else{
            kafkaTemplate.send(GmallConstants.KAFKA_TOPIC_EVENT,jsonString);
        }


        //为了不让服务器把success当做页面，需要添加个注解Responsebody
        return "success";
    }
}
