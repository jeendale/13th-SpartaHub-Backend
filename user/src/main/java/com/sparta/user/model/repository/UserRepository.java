package com.sparta.user.model.repository;

import com.sparta.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {

    boolean existsByNickname(String nickname);

    boolean existsBySlackId(String slackId);
}
