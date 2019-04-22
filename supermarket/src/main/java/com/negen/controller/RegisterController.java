package com.negen.controller;

import com.negen.dao.RegisterRepository;
import com.negen.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "register")
public class RegisterController {
    @Autowired
    RegisterRepository registerRepository;

    @RequestMapping(value = "register")
    public String register(ModelMap map, HttpServletRequest request) {
        String userName = request.getParameter("userName");
        String userPass = request.getParameter("userPass");
        User newUser = new User();
        newUser.setUserName(userName);
        newUser.setUserPass(userPass);
        User u = registerRepository.save(newUser);
        System.out.println("user:" + u.toString());
        map.addAttribute("result", "注册成功");
        return "result";
    }
}
