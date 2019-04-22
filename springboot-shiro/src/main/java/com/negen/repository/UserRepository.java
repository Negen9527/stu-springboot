package com.negen.repository;

import com.negen.entity.User;

public interface UserRepository extends BaseRepository<User, Long> {
    User findByName(String name);

    User findOneById(Long id);
}
