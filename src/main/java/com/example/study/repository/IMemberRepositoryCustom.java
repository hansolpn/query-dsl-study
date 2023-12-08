package com.example.study.repository;

import com.example.study.entity.Member;

import java.util.List;

public interface IMemberRepositoryCustom {
    List<Member> findByName(String name);

    List<Member> findUser(String nameParam, Integer ageParam);
}
