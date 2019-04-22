package com.negen.controller;

import com.negen.dao.UserRepository;
import com.negen.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequestMapping(value = "user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "list")
    public String listAllUser(ModelMap map) {
        List<User> users = userRepository.findAll();
        map.addAttribute("users", users);
        return "userList";
    }

    @Transactional
    @RequestMapping(value = "delete")
    public String deleteUser(ModelMap map, HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean deleteResult = userRepository.deleteUserById(id) == 1 ? true : false;
        if (deleteResult) {
            return "redirect:list";
        } else {
            map.addAttribute("result", "删除失败");
            return "result";
        }
    }

    @RequestMapping(value = "modify")
    public String modifyUser(ModelMap map, HttpServletRequest request) {
        int id;
        String userName;
        String userPass;
        id = Integer.parseInt(request.getParameter("id"));
        if ("" == request.getParameter("userName")) {
            User oldUser = userRepository.findUserById(id);
            map.addAttribute("user", oldUser);
            return "modifyUser";
        } else {
            userName = request.getParameter("userName");
            userPass = request.getParameter("userPass");
            User oldUser = userRepository.findUserById(id);
            oldUser.setUserName(userName);
            oldUser.setUserPass(userPass);
            userRepository.save(oldUser);
            map.addAttribute("result", "修改成功");
            return "result";
        }

    }


}
