package com.sparta.ai.model.repository;

import com.sparta.ai.model.entity.AiMessage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiMessageRepository extends JpaRepository<AiMessage, UUID>, AiMessageRepositoryCustom {
}
