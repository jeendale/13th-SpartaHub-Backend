package com.sparta.user.model.repository;

import com.sparta.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByNickname(String nickname);

    Optional<User> findBySlackId(String slackId);
}
