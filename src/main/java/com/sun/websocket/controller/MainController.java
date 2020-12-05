package com.sun.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author sunjx
 * @Date 2020-12-05 14:57
 * @Description
 **/
@RequestMapping("/")
@Controller
public class MainController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }
}
