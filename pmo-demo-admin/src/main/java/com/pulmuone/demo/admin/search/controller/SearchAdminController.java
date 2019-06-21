package com.pulmuone.demo.admin.search.controller;

import com.pulmuone.demo.common.property.UrlProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchAdminController {
    @Autowired
    UrlProperty urlProperty;


    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/search-demo")
    public String searchDemo(Model model){
        model.addAttribute("apiUrl", urlProperty.getApi());
        return "searchDemo";
    }

    @RequestMapping("/analyze")
    public String analyze(Model model){
        model.addAttribute("apiUrl", urlProperty.getApi());
        return "analyze";
    }

    @RequestMapping("/boosting")
    public String boosting(Model model){
        model.addAttribute("apiUrl", urlProperty.getApi());
        return "boosting";
    }

    @RequestMapping("/dic/synonym")
    public String synonymDictionary(Model model){
        model.addAttribute("apiUrl", urlProperty.getApi());
        return "synonymDictionary";
    }

    @RequestMapping("/dic/user")
    public String UserDictionary(Model model){
        model.addAttribute("apiUrl", urlProperty.getApi());
        return "userDictionary";
    }

    @RequestMapping("/dic/stop")
    public String stopDictionary(Model model){
        model.addAttribute("apiUrl", urlProperty.getApi());
        return "stopDictionary";
    }




}
