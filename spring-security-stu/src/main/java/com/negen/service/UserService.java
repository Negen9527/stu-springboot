package com.negen.service;

import com.negen.model.User;
import com.negen.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserService implements UserDetailsService {
    static Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.info("param--userName===>" + userName);
        User user = userRepository.findByUserName(userName);
        if(user == null){
            log.info("user===>" + user);
            throw new UsernameNotFoundException("用户名不存在");
        }
        log.info("userName===>" + user.getUsername());
        log.info("userPass===>" + user.getPassword());
        return user;
    }
}
