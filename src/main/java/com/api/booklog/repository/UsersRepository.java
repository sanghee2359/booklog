package com.api.booklog.repository;


import com.api.booklog.domain.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findById(Long userId);
    Optional<UserEntity> findByEmail(String email);
    @Query(value="SELECT COUNT(u) FROM UserEntity u WHERE u.name = :name OR u.email = :email")
    Integer findByNameOrEmail(@Param("name") String name, @Param("email") String email);

}
