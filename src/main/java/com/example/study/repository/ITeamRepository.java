package com.example.study.repository;

import com.example.study.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITeamRepository
        extends JpaRepository<Team, Long> {
}
