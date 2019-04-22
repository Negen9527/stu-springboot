package com.negen.controller;

import com.negen.dao.UserRepository;
import com.negen.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "user")
public class LoginController {
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "../login")
    public String logon() {
        return "/login";
    }

    /**
     * 登陆验证
     *
     * @param map
     * @param request
     * @return
     */
    @RequestMapping(value = "login")
    public String login(ModelMap map, HttpServletRequest request) {
        String userName = request.getParameter("userName");
        String userPass = request.getParameter("userPass");
        User cUser = new User();
        cUser.setUserName(userName);
        cUser.setUserPass(userPass);
        User rUser = userRepository.findByUserNameAndUserPass(userName, userPass);
        if (null != rUser) {
            map.addAttribute("result", "登陆成功");
        } else {
            map.addAttribute("result", "登陆失败");
        }
        return "/result";
    }

}
