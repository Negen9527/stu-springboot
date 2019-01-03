package com.negen.service.impl;

import com.negen.entity.Permission;
import com.negen.entity.Role;
import com.negen.entity.User;
import com.negen.repository.RoleRepository;
import com.negen.repository.UserRepository;
import com.negen.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LoginServiceImpl implements ILoginService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User addUser(Map<String, Object> map) {
        User user = new User();
        user.setName(map.get("name").toString());
        user.setPassword(map.get("password").toString());
        userRepository.save(user);
        return user;
    }

    @Override
    public Role addRole(Map<String, Object> map) {
        User user = userRepository.findOneById(Long.valueOf(map.get("userId").toString()));
        Role role = new Role();
        role.setRoleName(map.get("roleName").toString());
        role.setUser(user);
        Permission permissionOne = new Permission();
        permissionOne.setPermission("create");
        permissionOne.setRole(role);
        Permission permissionTwo = new Permission();
        permissionTwo.setPermission("update");
        permissionTwo.setRole(role);
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(permissionOne);
        permissions.add(permissionTwo);
        role.setPermissions(permissions);
        roleRepository.save(role);
        return role;
    }

    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }
}
