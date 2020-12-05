package com.sun.websocket.rest;

import cn.hutool.core.util.RandomUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author sunjx
 * @Date 2020-12-05 14:48
 * @Description
 **/
@RequestMapping("/websocket")
@RestController
public class WebsocketRest {

    @GetMapping("/token")
    public String wsToken(){
        return RandomUtil.randomString(10);
    }
}
