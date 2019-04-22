package com.negen.service;

import com.negen.entity.Role;
import com.negen.entity.User;

import java.util.Map;

public interface ILoginService {
    User addUser(Map<String, Object> map);

    Role addRole(Map<String, Object> map);

    User findByName(String name);
}
