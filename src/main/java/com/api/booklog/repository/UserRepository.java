package com.api.booklog.repository;


import com.api.booklog.domain.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Long> {

    Optional<Users> findByEmailAndPassword(String email, String password);
}
