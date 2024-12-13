package com.sparta.Hub.model.repository;

import com.sparta.Hub.model.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> ,HubCustomRepository{

}
