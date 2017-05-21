package com.nekolr.simvc.controller;

import com.nekolr.simvc.annotation.Controller;
import com.nekolr.simvc.annotation.RequestMapping;
import com.nekolr.simvc.annotation.RequestMethod;
import com.nekolr.simvc.annotation.ResponseBody;

/**
 *
 */
@Controller
@RequestMapping(value = "/user")
public class LoginController {
    @RequestMapping(value = "/gologin.do",method = RequestMethod.GET)
    public String gologin(){
        return "/html/login";
    }

    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public String login(){
        return "success";
    }
}
