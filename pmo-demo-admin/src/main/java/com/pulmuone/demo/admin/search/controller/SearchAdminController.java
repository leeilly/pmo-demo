package com.pulmuone.demo.admin.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchAdminController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/search-demo")
    public String searchDemo(){
        return "searchDemo";
    }


    @RequestMapping("/analyze")
    public String analyze(){
        return "analyze";
    }

}
