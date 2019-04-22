package com.negen.controller;

import com.negen.service.ILoginService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "account")
public class LoginController {
    @Autowired
    private ILoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam("name") String name,
                        @RequestParam("password") String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
                name,
                password
        );
        try {
            subject.login(usernamePasswordToken);
        } catch (Exception e) {
            System.out.println("========>登陆失败");
        }
        return "redirect:/html/index.html";
    }

}
