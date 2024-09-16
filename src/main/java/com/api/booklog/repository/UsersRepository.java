package com.api.booklog.repository;


import com.api.booklog.domain.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);
    @Query("SELECT u FROM Users u WHERE u.name = :name OR u.email = :email")
    Optional<Users> findByNameOrEmail(@Param("name") String name, @Param("email") String email);

}
