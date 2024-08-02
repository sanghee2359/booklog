package com.api.booklog.repository;


import com.api.booklog.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailAndPassword(String email, String password);
    Optional<Users> findByEmail(String email);
}
