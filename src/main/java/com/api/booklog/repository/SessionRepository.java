package com.api.booklog.repository;


import com.api.booklog.domain.Session;
import com.api.booklog.domain.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session, Long> {

}
