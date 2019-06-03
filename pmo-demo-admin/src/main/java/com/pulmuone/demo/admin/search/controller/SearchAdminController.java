package com.pulmuone.demo.admin.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchAdminController {

    @RequestMapping("/")
    public String hello(){
        return "index";
    }
}
