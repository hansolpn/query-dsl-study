package com.example.study.repository;

import com.example.study.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMemberRepository
        extends JpaRepository<Member, Long>,
        IMemberRepositoryCustom {
}
