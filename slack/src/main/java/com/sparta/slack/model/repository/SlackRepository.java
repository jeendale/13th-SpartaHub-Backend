package com.sparta.slack.model.repository;

import com.sparta.slack.model.entity.SlackHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackRepository extends JpaRepository<SlackHistory, UUID> {
}
