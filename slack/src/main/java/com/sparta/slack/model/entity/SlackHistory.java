package com.sparta.slack.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_slack_history")
public class SlackHistory extends Audit{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID slackHistoryId;

    @Column(nullable = false)
    private String username;

    @Lob
    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime sentAt;
}
