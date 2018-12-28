package com.negen.dao;

import com.negen.entity.User;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserNameAndUserPass(String userName, String userPass);
    int deleteUserById(int id);
    User findUserById(int id);

}
